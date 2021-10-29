package io.github.asvanberg.donkey.serializing;

import io.github.asvanberg.donkey.serializing.RegisteredSerializer.Priority;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.JsonbSerializer;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class Serializers
{
    private final Collection<RegisteredSerializer> serializers = new ArrayList<>();

    public Serializers(final JsonbConfig config)
    {
        initializeSerializers(config);
    }

    private void initializeSerializers(final JsonbConfig config)
    {
        final JsonbSerializer<?>[] providedSerializers = config.getProperty(JsonbConfig.SERIALIZERS)
                                                               .map(s -> (JsonbSerializer<?>[]) s)
                                                               .orElse(new JsonbSerializer[0]);
        for (JsonbSerializer<?> providedSerializer : providedSerializers)
        {
            getTypeArgumentForInterface(providedSerializer, JsonbSerializer.class)
                    .forEach(handledType ->
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
    }

    private static Stream<Class<?>> getTypeArgumentForInterface(final Object object, final Class<?> interfaceClass)
    {
        return Stream.<Class<?>>iterate(object.getClass(), Predicate.not(Object.class::equals), Class::getSuperclass)
                     .filter(Objects::nonNull)
                     .map(Class::getGenericInterfaces)
                     .flatMap(Arrays::stream)
                     .filter(ParameterizedType.class::isInstance)
                     .map(ParameterizedType.class::cast)
                     .filter(pt -> interfaceClass.equals(pt.getRawType()))
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