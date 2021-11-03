package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;

public class Deserializer {
    private final Map<Class<?>, JsonbDeserializer<?>> deserializers
            = new HashMap<>();
    private final Map<Class<?>, Function<Type[], JsonbDeserializer<?>>> parameterizedDeserializers
            = new HashMap<>();

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
        parameterizedDeserializers.put(ArrayList.class, ListDeserializer::new);
        parameterizedDeserializers.put(HashMap.class, MapDeserializer::new);
        parameterizedDeserializers.put(Optional.class, OptionalDeserializer::new);
    }

    private void initializeAdapters(final JsonbConfig config)
    {
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

    @SuppressWarnings("unchecked")
    private <T> JsonbDeserializer<T> getJsonbDeserializer(
            final Type runtimeType)
    {
        return (JsonbDeserializer<T>) getUncheckedJsonbDeserializer(runtimeType);
    }

    private static final Map<Class<?>, TemporalQuery<?>> TEMPORAL_QUERIES_BY_TYPE =
            Map.of(LocalDate.class, LocalDate::from,
                   LocalTime.class, LocalTime::from,
                   LocalDateTime.class, LocalDateTime::from,
                   OffsetDateTime.class, OffsetDateTime::from);

    @SuppressWarnings({"unchecked", "rawtypes"})
    private JsonbDeserializer<?> getUncheckedJsonbDeserializer(
            final Type runtimeType)
    {
        if (runtimeType instanceof Class<?> clazz) {
            if (Enum.class.isAssignableFrom(clazz)) {
                return new EnumDeserializer(clazz);
            }
            return deserializers.computeIfAbsent(clazz, ObjectDeserializer::of);
        }
        else if (runtimeType instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType() instanceof Class<?> clazz) {
                for (var entry : parameterizedDeserializers.entrySet()) {
                    if (clazz.isAssignableFrom(entry.getKey())) {
                        return entry.getValue()
                                    .apply(parameterizedType.getActualTypeArguments());
                    }
                }
            }
        }
        else if (runtimeType instanceof CustomDateFormatType customDateFormatType) {
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

    public <T> T deserialize(final JsonParser parser, final Type runtimeType) {
        return new DeserializationProcess().deserialize(runtimeType, parser);
    }

    private class DeserializationProcess implements DeserializationContext {
        @Override
        public <T> T deserialize(final Class<T> clazz, final JsonParser parser) {
            return deserialize((Type) clazz, parser);
        }

        @Override
        public <T> T deserialize(final Type type, final JsonParser parser) {
            if (adapters.containsKey(type)) {
                return deserializeAdapted(type, parser);
            }
            if (type instanceof Class<?> clazz) {
                final JsonbTypeAdapter jsonbTypeAdapter
                        = clazz.getAnnotation(JsonbTypeAdapter.class);
                if (jsonbTypeAdapter != null) {
                    registerTypeLevelAdapter(jsonbTypeAdapter);
                    return deserialize(type, parser);
                }
            }
            final JsonbDeserializer<T> jsonbDeserializer = getJsonbDeserializer(type);
            return jsonbDeserializer.deserialize(parser, this, type);
        }

        private void registerTypeLevelAdapter(final JsonbTypeAdapter jsonbTypeAdapter)
        {
            final JsonbAdapter<?, ?> jsonbAdapter = Util.createJsonbAdapter(jsonbTypeAdapter);
            Util.getParameterizedType(jsonbAdapter, JsonbAdapter.class)
                        .ifPresent(parameterizedType ->
                                           registerAdapter(jsonbAdapter, parameterizedType));
        }

        @SuppressWarnings("unchecked")
        private <T> T deserializeAdapted(final Type type, final JsonParser parser)
        {
            try {
                final Adapter adapter = adapters.get(type);
                final Object adaptedValue = deserialize(adapter.adaptedType(), parser);
                final JsonbAdapter<T, Object> jsonbAdapter
                        = (JsonbAdapter<T, Object>) adapter.jsonbAdapter();
                return jsonbAdapter.adaptFromJson(adaptedValue);
            } catch (Exception e) {
                throw new AdaptingFailedException(e);
            }
        }
    }
}
