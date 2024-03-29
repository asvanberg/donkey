package io.github.asvanberg.donkey.serializing;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import io.github.asvanberg.donkey.internal.NullAdapter;
import io.github.asvanberg.donkey.internal.URIStringJsonbAdapter;
import io.github.asvanberg.donkey.internal.UUIDStringJsonbAdapter;
import io.github.asvanberg.donkey.internal.Util;
import io.github.asvanberg.donkey.codecs.BigDecimalCodec;
import io.github.asvanberg.donkey.codecs.BigIntegerCodec;
import io.github.asvanberg.donkey.codecs.BooleanCodec;
import io.github.asvanberg.donkey.codecs.DoubleCodec;
import io.github.asvanberg.donkey.codecs.FloatCodec;
import io.github.asvanberg.donkey.codecs.InstantCodec;
import io.github.asvanberg.donkey.codecs.IntegerCodec;
import io.github.asvanberg.donkey.codecs.JsonValueSerializer;
import io.github.asvanberg.donkey.codecs.LocalDateCodec;
import io.github.asvanberg.donkey.codecs.LocalDateTimeCodec;
import io.github.asvanberg.donkey.codecs.LocalTimeCodec;
import io.github.asvanberg.donkey.codecs.LongCodec;
import io.github.asvanberg.donkey.codecs.OffsetDateTimeCodec;
import io.github.asvanberg.donkey.codecs.OptionalDoubleCodec;
import io.github.asvanberg.donkey.codecs.OptionalIntCodec;
import io.github.asvanberg.donkey.codecs.OptionalLongCodec;
import io.github.asvanberg.donkey.codecs.StringCodec;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class Serializers implements SerializationContext
{
    private final Map<Class<?>, JsonbSerializer<?>> serializers = new HashMap<>();
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
        registerBuiltInSerializers();
        final JsonbSerializer<?>[] providedSerializers = config.getProperty(JsonbConfig.SERIALIZERS)
                                                               .map(s -> (JsonbSerializer<?>[]) s)
                                                               .orElse(new JsonbSerializer[0]);
        for (JsonbSerializer<?> providedSerializer : providedSerializers)
        {
            Util.getFirstTypeArgumentForInterface(providedSerializer, JsonbSerializer.class)
                .ifPresent(handledType -> serializers.put(handledType, providedSerializer));
        }
    }

    private void registerBuiltInSerializers()
    {
        serializers.put(String.class, StringCodec.INSTANCE);
        serializers.put(Integer.class, IntegerCodec.INSTANCE);
        serializers.put(BigDecimal.class, BigDecimalCodec.INSTANCE);
        serializers.put(BigInteger.class, BigIntegerCodec.INSTANCE);
        serializers.put(Boolean.class, BooleanCodec.INSTANCE);
        serializers.put(Long.class, LongCodec.INSTANCE);
        serializers.put(Collection.class, CollectionSerializer.INSTANCE);
        serializers.put(Double.class, DoubleCodec.INSTANCE);
        serializers.put(Float.class, FloatCodec.INSTANCE);
        serializers.put(JsonValue.class, JsonValueSerializer.INSTANCE);
        serializers.put(OptionalInt.class, OptionalIntCodec.INSTANCE);
        serializers.put(OptionalLong.class, OptionalLongCodec.INSTANCE);
        serializers.put(OptionalDouble.class, OptionalDoubleCodec.INSTANCE);
        serializers.put(Optional.class, OptionalSerializer.INSTANCE);
        serializers.put(Map.class, MapSerializer.INSTANCE);
        serializers.put(Instant.class, InstantCodec.INSTANCE);
        serializers.put(OffsetDateTime.class, OffsetDateTimeCodec.INSTANCE);
        serializers.put(LocalDateTime.class, LocalDateTimeCodec.INSTANCE);
        serializers.put(LocalDate.class, LocalDateCodec.INSTANCE);
        serializers.put(LocalTime.class, LocalTimeCodec.INSTANCE);
        serializers.put(Enum.class, EnumSerializer.INSTANCE);
        serializers.put(TemporalAccessorProperty.class, new TemporalAccessorSerializer(locale));
    }

    private void initializeAdapters(final JsonbConfig config)
    {
        adapters.put(URI.class, URIStringJsonbAdapter.INSTANCE);
        adapters.put(UUID.class, UUIDStringJsonbAdapter.INSTANCE);
        final JsonbAdapter<?, ?>[] providedAdapters
                = config.getProperty(JsonbConfig.ADAPTERS)
                        .map(s -> (JsonbAdapter<?, ?>[]) s)
                        .orElse(new JsonbAdapter[0]);
        for (JsonbAdapter<?, ?> providedAdapter : providedAdapters) {
            Util.getFirstTypeArgumentForInterface(providedAdapter, JsonbAdapter.class)
                .ifPresent(clazz -> adapters.put(clazz, providedAdapter));
        }
    }

    @Override
    public <T> void serialize(final T object, final JsonGenerator generator)
    {
        if (object != null) {
            final Class<?> clazz = object.getClass();
            JsonbAdapter<T, ?> jsonbAdapter
                    = (JsonbAdapter<T, ?>) adapters.get(clazz);
            if (jsonbAdapter == null) {
                jsonbAdapter = (JsonbAdapter<T, ?>) getAdapter(clazz);
                adapters.put(clazz, jsonbAdapter);
            }
            if (jsonbAdapter != NullAdapter.INSTANCE) {
                final Object adapted;
                try {
                    adapted = jsonbAdapter.adaptToJson(object);
                }
                catch (Exception e) {
                    throw new AdaptingFailedException(e);
                }
                serialize(adapted, generator);
            }
            else {
                final JsonbSerializer<Object> serializer
                        = (JsonbSerializer<Object>) getJsonbSerializer(object.getClass());
                serializer.serialize(object, generator, this);
            }
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

    private JsonbAdapter<?, ?> getAdapter(final Class<?> clazz)
    {
        final JsonbTypeAdapter jsonbTypeAdapter
                = clazz.getAnnotation(JsonbTypeAdapter.class);
        if (jsonbTypeAdapter != null) {
            return Util.createJsonbAdapter(jsonbTypeAdapter);
        }
        else {
            return NullAdapter.INSTANCE;
        }
    }

    public JsonbSerializer<?> getJsonbSerializer(final Class<?> aClass)
    {
        final JsonbSerializer<?> serializer = serializers.get(aClass);
        if (serializer != null) {
            return serializer;
        }
        for (final var entry : serializers.entrySet()) {
            if (entry.getKey().isAssignableFrom(aClass)) {
                serializers.put(aClass, entry.getValue());
                return entry.getValue();
            }
        }
        return serializers.computeIfAbsent(aClass, ObjectSerializer::of);
    }

}