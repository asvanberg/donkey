package io.github.asvanberg.donkey.serializing;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import io.github.asvanberg.donkey.internal.Util;
import io.github.asvanberg.donkey.serializing.RegisteredSerializer.Priority;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

@SuppressWarnings("unchecked")
public class Serializers implements SerializationContext
{
    private final Collection<RegisteredSerializer> serializers = new ArrayList<>();
    private final Map<Class<?>, JsonbAdapter<?, ?>> adapters = new HashMap<>();
    private final Locale locale;

    public Serializers(final JsonbConfig config)
    {
        this.locale = config.getProperty(JsonbConfig.LOCALE)
                            .map(Locale.class::cast)
                            .orElseGet(Locale::getDefault);
        initializeSerializers(config);
        initializeAdapters(config);
    }

    private void initializeSerializers(final JsonbConfig config)
    {
        final JsonbSerializer<?>[] providedSerializers = config.getProperty(JsonbConfig.SERIALIZERS)
                                                               .map(s -> (JsonbSerializer<?>[]) s)
                                                               .orElse(new JsonbSerializer[0]);
        for (JsonbSerializer<?> providedSerializer : providedSerializers)
        {
            getFirstTypeArgumentForInterface(providedSerializer, JsonbSerializer.class)
                    .ifPresent(handledType ->
                                     register(providedSerializer, handledType, Priority.CONFIGURED));
        }
        register(StringSerializer.INSTANCE, String.class, Priority.BUILT_IN);
        register(IntSerializer.INSTANCE, Integer.class, Priority.BUILT_IN);
        register(BigDecimalSerializer.INSTANCE, BigDecimal.class, Priority.BUILT_IN);
        register(BigIntegerSerializer.INSTANCE, BigInteger.class, Priority.BUILT_IN);
        register(BooleanSerializer.INSTANCE, Boolean.class, Priority.BUILT_IN);
        register(LongSerializer.INSTANCE, Long.class, Priority.BUILT_IN);
        register(CollectionSerializer.INSTANCE, Collection.class, Priority.BUILT_IN);
        register(DoubleSerializer.INSTANCE, Double.class, Priority.BUILT_IN);
        register(FloatSerializer.INSTANCE, Float.class, Priority.BUILT_IN);
        register(JsonValueSerializer.INSTANCE, JsonValue.class, Priority.BUILT_IN);
        register(OptionalIntSerializer.INSTANCE, OptionalInt.class, Priority.BUILT_IN);
        register(OptionalLongSerializer.INSTANCE, OptionalLong.class, Priority.BUILT_IN);
        register(OptionalDoubleSerializer.INSTANCE, OptionalDouble.class, Priority.BUILT_IN);
        register(OptionalSerializer.INSTANCE, Optional.class, Priority.BUILT_IN);
        register(MapSerializer.INSTANCE, Map.class, Priority.BUILT_IN);
        register(InstantSerializer.INSTANCE, Instant.class, Priority.BUILT_IN);
        register(OffsetDateTimeSerializer.INSTANCE, OffsetDateTime.class, Priority.BUILT_IN);
        register(LocalDateTimeSerializer.INSTANCE, LocalDateTime.class, Priority.BUILT_IN);
        register(LocalDateSerializer.INSTANCE, LocalDate.class, Priority.BUILT_IN);
        register(LocalTimeSerializer.INSTANCE, LocalTime.class, Priority.BUILT_IN);
        register(new TemporalAccessorSerializer(locale), TemporalAccessorProperty.class, Priority.BUILT_IN);
    }

    private static Optional<Class<?>> getFirstTypeArgumentForInterface(
            final Object object,
            final Class<?> interfaceClass)
    {
        return Util.getParameterizedType(object, interfaceClass)
                   // JsonbDeserializer only has one argument
                   // JsonbSerializer only has one argument
                   // JsonbAdapter we only care about the source (first argument)
                   .map(pt -> pt.getActualTypeArguments()[0])
                   .filter(Class.class::isInstance)
                   .map(Class.class::cast);
    }

    private void register(
            final JsonbSerializer<?> providedSerializer,
            final Class<?> handledType,
            final Priority configured)
    {
        serializers.add(new RegisteredSerializer(configured, handledType, providedSerializer));
    }

    private void initializeAdapters(final JsonbConfig config)
    {
        final JsonbAdapter<?, ?>[] providedAdapters
                = config.getProperty(JsonbConfig.ADAPTERS)
                        .map(s -> (JsonbAdapter<?, ?>[]) s)
                        .orElse(new JsonbAdapter[0]);
        for (JsonbAdapter<?, ?> providedAdapter : providedAdapters) {
            getFirstTypeArgumentForInterface(providedAdapter, JsonbAdapter.class)
                    .ifPresent(clazz -> adapters.put(clazz, providedAdapter));
        }
    }

    @Override
    public <T> void serialize(final T object, final JsonGenerator generator)
    {
        if (object != null) {
            if (adapters.containsKey(object.getClass())) {
                final JsonbAdapter<T, ?> adapter = (JsonbAdapter<T, ?>) adapters.get(object.getClass());
                try {
                    serialize(adapter.adaptToJson(object), generator);
                    return;
                }
                catch (Exception e) {
                    throw new AdaptingFailedException(e);
                }
            }
            final JsonbSerializer<Object> serializer = getJsonbSerializer(object.getClass());
            serializer.serialize(object, generator, this);
        }
        else {
            generator.writeNull();
        }
    }

    @Override
    public <T> void serialize(final String key, final T object, final JsonGenerator generator)
    {
        generator.writeKey(key);
        serialize(object, generator);
    }

    public JsonbSerializer<Object> getJsonbSerializer(final Class<?> aClass)
    {
        return serializers.stream()
                          .filter(rs -> rs.supports(aClass))
                          .max(Comparator.comparing(RegisteredSerializer::priority))
                          .map(RegisteredSerializer::serializer)
                          .map(s -> (JsonbSerializer<Object>) s)
                          .orElse(ObjectSerializer.INSTANCE);
    }
}