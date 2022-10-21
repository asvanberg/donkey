package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import io.github.asvanberg.donkey.internal.URIStringJsonbAdapter;
import io.github.asvanberg.donkey.internal.UUIDStringJsonbAdapter;
import io.github.asvanberg.donkey.internal.Util;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;

public class Deserializer implements DeserializationContext {
    private final Map<Class<?>, JsonbDeserializer<?>> deserializers
            = new HashMap<>();
    private final Map<Type, JsonbDeserializer<?>> userDeserializers
            = new HashMap<>();
    private final List<ParameterizedDeserializer> parameterizedDeserializers
            = new ArrayList<>();
    /**
     * Resolved deserializers are the complete package including any necessary adapting.
     * They are stored here for performance reasons instead of doing the lookup every
     * deserialization request.
     */
    private final Map<Type, JsonbDeserializer<?>> resolved = new HashMap<>();

    private final Locale defaultLocale;
    private final Map<LocalizedPattern, DateTimeFormatter> dateTimeFormatters = new HashMap<>();

    private record LocalizedPattern(String pattern, Locale locale) {
        DateTimeFormatter createFormatter() {
            return DateTimeFormatter.ofPattern(pattern, locale);
        }
    }

    private final Map<Type, Adapter> adapters = new HashMap<>();

    private record Adapter(Type adaptedType, JsonbAdapter<?, ?> jsonbAdapter) {
    }

    public Deserializer(final JsonbConfig config) {
        this.defaultLocale = config.getProperty(JsonbConfig.LOCALE)
                                   .map(Locale.class::cast)
                                   .orElseGet(Locale::getDefault);
        initializeAdapters(config);
        initializeDeserializers(config);
    }

    private void initializeDeserializers(final JsonbConfig config)
    {
        registerBuiltInDeserializers();
        final JsonbDeserializer<?>[] providedDeserializers
                = config.getProperty(JsonbConfig.DESERIALIZERS)
                        .map(s -> (JsonbDeserializer<?>[]) s)
                        .orElse(new JsonbDeserializer[0]);
        for (JsonbDeserializer<?> providedDeserializer : providedDeserializers) {
            Util.getFirstTypeArgumentForInterface(providedDeserializer, JsonbDeserializer.class)
                .ifPresent(handledType -> userDeserializers.put(handledType, providedDeserializer));
        }
    }

    private void registerBuiltInDeserializers()
    {
        deserializers.put(Integer.class, IntDeserializer.INSTANCE);
        deserializers.put(int.class, IntDeserializer.INSTANCE);
        deserializers.put(Float.class, FloatDeserializer.INSTANCE);
        deserializers.put(float.class, FloatDeserializer.INSTANCE);
        deserializers.put(Double.class, DoubleDeserializer.INSTANCE);
        deserializers.put(double.class, DoubleDeserializer.INSTANCE);
        deserializers.put(String.class, StringDeserializer.INSTANCE);
        deserializers.put(Long.class, LongDeserializer.INSTANCE);
        deserializers.put(long.class, LongDeserializer.INSTANCE);
        deserializers.put(boolean.class, BooleanDeserializer.INSTANCE);
        deserializers.put(Boolean.class, BooleanDeserializer.INSTANCE);
        deserializers.put(OptionalInt.class, OptionalIntDeserializer.INSTANCE);
        deserializers.put(OptionalLong.class, OptionalLongDeserializer.INSTANCE);
        deserializers.put(OptionalDouble.class, OptionalDoubleDeserializer.INSTANCE);
        deserializers.put(Instant.class, InstantDeserializer.INSTANCE);
        deserializers.put(OffsetDateTime.class, OffsetDateTimeDeserializer.INSTANCE);
        deserializers.put(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        deserializers.put(LocalDate.class, LocalDateDeserializer.INSTANCE);
        deserializers.put(LocalTime.class, LocalTimeDeserializer.INSTANCE);
        deserializers.put(BigInteger.class, BigIntegerDeserializer.INSTANCE);
        parameterizedDeserializers.add(new ParameterizedDeserializer(ArrayList.class, ListDeserializer::new));
        parameterizedDeserializers.add(new ParameterizedDeserializer(HashMap.class, MapDeserializer::new));
        parameterizedDeserializers.add(new ParameterizedDeserializer(Optional.class, OptionalDeserializer::new));
        parameterizedDeserializers.add(new ParameterizedDeserializer(HashSet.class, SetDeserializer::of));
    }

    private void initializeAdapters(final JsonbConfig config)
    {
        adapters.put(URI.class, new Adapter(String.class, URIStringJsonbAdapter.INSTANCE));
        adapters.put(UUID.class, new Adapter(String.class, UUIDStringJsonbAdapter.INSTANCE));
        final JsonbAdapter<?, ?>[] providedAdapters
                = config.getProperty(JsonbConfig.ADAPTERS)
                        .map(s -> (JsonbAdapter<?, ?>[]) s)
                        .orElse(new JsonbAdapter[0]);
        for (JsonbAdapter<?, ?> providedAdapter : providedAdapters) {
            Util.getParameterizedType(providedAdapter, JsonbAdapter.class)
                    .ifPresent(parameterizedType ->
                                       registerAdapter(providedAdapter, parameterizedType));
        }
    }

    private void registerAdapter(
            final JsonbAdapter<?, ?> providedAdapter,
            final ParameterizedType parameterizedType)
    {
        final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        adapters.put(actualTypeArguments[0], new Adapter(actualTypeArguments[1], providedAdapter));
    }

    private static final Map<Class<?>, TemporalQuery<?>> TEMPORAL_QUERIES_BY_TYPE =
            Map.of(LocalDate.class, LocalDate::from,
                   LocalTime.class, LocalTime::from,
                   LocalDateTime.class, LocalDateTime::from,
                   OffsetDateTime.class, OffsetDateTime::from);

    private JsonbDeserializer<?> getResolved(Type type) {
        // can not use computeIfAbsent due to the recursive nature of the resolve(Type) method
        JsonbDeserializer<?> deserializer = resolved.get(type);
        if (deserializer != null) {
            return deserializer;
        }
        JsonbDeserializer<?> resolvedDeserializer = resolve(type);
        resolved.put(type, resolvedDeserializer);
        return resolvedDeserializer;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private JsonbDeserializer<?> resolve(
            final Type runtimeType)
    {
        // 1. Check user provided deserializer
        JsonbDeserializer<?> deserializer = userDeserializers.get(runtimeType);
        if (deserializer != null) {
            return deserializer;
        }
        // 2. Check user provided adapter
        Adapter adapter = adapters.computeIfAbsent(runtimeType, this::getAdapter);
        if (adapter != null) {
            JsonbDeserializer<?> adapted = getResolved(adapter.adaptedType());
            JsonbAdapter<Object, Object> jsonbAdapter = (JsonbAdapter<Object, Object>) adapter.jsonbAdapter();
            return new AdaptedJsonbDeserializer(adapted, jsonbAdapter, adapter.adaptedType());
        }
        // 3. Check known handled types
        if (runtimeType instanceof Class<?> clazz) {
            if (Enum.class.isAssignableFrom(clazz)) {
                return new EnumDeserializer(clazz);
            }
            JsonbDeserializer<?> known = deserializers.get(clazz);
            if (known != null) {
                return known;
            }
            return ObjectDeserializer.of(clazz);
        }
        else if (runtimeType instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType() instanceof Class<?> clazz) {
                for (var entry : parameterizedDeserializers) {
                    if (clazz.isAssignableFrom(entry.clazz())) {
                        return entry.factory()
                                    .apply(parameterizedType.getActualTypeArguments());
                    }
                }
            }
        }
        else if (runtimeType instanceof CustomDateFormatType customDateFormatType) {
            if (Objects.equals(JsonbDateFormat.TIME_IN_MILLIS, customDateFormatType.pattern())) {
                return EpochMilliInstantDeserializer.INSTANCE;
            }
            final Locale locale =
                    Objects.equals(JsonbDateFormat.DEFAULT_LOCALE, customDateFormatType.locale())
                            ? this.defaultLocale
                            : Locale.forLanguageTag(customDateFormatType.locale());
            final DateTimeFormatter dateTimeFormatter = dateTimeFormatters.computeIfAbsent(
                    new LocalizedPattern(customDateFormatType.pattern(), locale),
                    LocalizedPattern::createFormatter);
            return new CustomDateFormatDeserializer<>(
                    dateTimeFormatter,
                    TEMPORAL_QUERIES_BY_TYPE.get(customDateFormatType.temporalQuery()));
        }
        throw new JsonbException(runtimeType + " not supported");
    }

    @Override
    public <T> T deserialize(final Class<T> clazz, final JsonParser parser) {
        return deserialize((Type) clazz, parser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(final Type type, final JsonParser parser) {
        final JsonbDeserializer<?> jsonbDeserializer
                = getResolved(type);
        return (T) jsonbDeserializer.deserialize(parser, this, type);
    }

    private Adapter getAdapter(final Type type)
    {
        final JsonbTypeAdapter jsonbTypeAdapter;
        if (type instanceof Class<?> clazz
                && (jsonbTypeAdapter = clazz.getAnnotation(JsonbTypeAdapter.class)) != null)
        {
            final JsonbAdapter<?, ?> jsonbAdapter = Util.createJsonbAdapter(jsonbTypeAdapter);
            final ParameterizedType parameterizedType
                    = Util.getParameterizedType(jsonbAdapter, JsonbAdapter.class)
                          .orElseThrow(() -> new AdaptingFailedException(null));
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return new Adapter(actualTypeArguments[1], jsonbAdapter);
        }
        else {
            return null;
        }
    }
}
