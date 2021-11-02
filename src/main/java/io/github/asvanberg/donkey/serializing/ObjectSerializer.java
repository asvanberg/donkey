package io.github.asvanberg.donkey.serializing;

import io.github.asvanberg.donkey.exceptions.NoPropertiesToSerializeException;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.logging.Logger;

public enum ObjectSerializer implements JsonbSerializer<Object> {
    INSTANCE;

    private static final Logger LOG = Logger.getLogger(ObjectSerializer.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void serialize(final Object obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.writeStartObject();
        boolean foundProperty = false;
        for (Method method : obj.getClass().getMethods()) {
            final JsonbProperty jsonbProperty = method.getAnnotation(JsonbProperty.class);
            if (jsonbProperty == null) {
                LOG.fine(() -> "Skipping method [%s] due to no JsonbProperty annotation".formatted(method.getName()));
                continue;
            }
            final String propertyName = jsonbProperty.value();
            if (propertyName.isEmpty()) {
                LOG.fine(() -> "Skipping method [%s] due to no value specified on JsonbProperty annotation".formatted(method.getName()));
                continue;
            }
            try {
                foundProperty = true;
                final Object value = method.invoke(obj);
                if (!jsonbProperty.nillable() && isNull(value)) {
                    LOG.finer(() -> "Skipping property [%s] due to null value and not nillable".formatted(propertyName));
                    continue;
                }
                final JsonbTypeSerializer jsonbTypeSerializer = method.getAnnotation(JsonbTypeSerializer.class);
                if (jsonbTypeSerializer != null) {
                    final JsonbSerializer<Object> serializer = jsonbTypeSerializer.value().getConstructor().newInstance();
                    generator.writeKey(propertyName);
                    serializer.serialize(value, generator, ctx);
                } else {
                    if (value instanceof TemporalAccessor temporalAccessor) {
                        final TemporalAccessorProperty temporalAccessorProperty =
                                buildTemporalAccessProperty(method, temporalAccessor);
                        ctx.serialize(propertyName, temporalAccessorProperty, generator);
                    }
                    else {
                        ctx.serialize(propertyName, value, generator);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                throw new JsonbException("Failed to serialize property [" + propertyName + "] of class [" + obj.getClass() + "]", e);
            }
        }
        if (!foundProperty) {
            throw new NoPropertiesToSerializeException(obj.getClass());
        }
        generator.writeEnd();
    }

    private TemporalAccessorProperty buildTemporalAccessProperty(
            final Method method,
            final TemporalAccessor temporalAccessor)
    {
        final JsonbDateFormat jsonbDateFormat = method.getAnnotation(JsonbDateFormat.class);
        final String pattern = jsonbDateFormat == null
                ? JsonbDateFormat.DEFAULT_FORMAT : jsonbDateFormat.value();
        final String locale = jsonbDateFormat == null
                ? JsonbDateFormat.DEFAULT_LOCALE : jsonbDateFormat.locale();
        return new TemporalAccessorProperty(pattern, locale, temporalAccessor);
    }

    private static boolean isNull(final Object value) {
        if (value instanceof Optional<?> optional) {
            return optional.isEmpty();
        }
        else if (value instanceof OptionalDouble optional) {
            return optional.isEmpty();
        }
        else if (value instanceof OptionalInt optional) {
            return optional.isEmpty();
        }
        else if (value instanceof OptionalLong optional) {
            return optional.isEmpty();
        }
        else {
            return value == null;
        }
    }
}
