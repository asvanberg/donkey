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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

class ObjectSerializer implements JsonbSerializer<Object> {

    record Property(String name, boolean nillable, Method method, JsonbSerializer<Object> serializer)
    {
    }

    @SuppressWarnings("unchecked")
    static JsonbSerializer<Object> of(Class<?> clazz) {
        // check if there's an apt generated serializer
        try {
            final String serializerClassName = clazz.getName() + "$donkey$apt$serializer";
            return (JsonbSerializer<Object>) Class.forName(serializerClassName)
                                                  .getConstructor().newInstance();
        } catch (Exception ignored) {}
        final List<Property> properties = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            final JsonbProperty jsonbProperty = method.getAnnotation(JsonbProperty.class);
            if (jsonbProperty == null) {
                continue;
            }
            final String propertyName = jsonbProperty.value();
            if (propertyName.isEmpty()) {
                // TODO: error in 2.x
                continue;
            }
            final boolean nillable = jsonbProperty.nillable();
            JsonbSerializer<Object> serializer = null;
            final JsonbTypeSerializer jsonbTypeSerializer = method.getAnnotation(JsonbTypeSerializer.class);
            if (jsonbTypeSerializer != null) {
                try {
                    serializer = jsonbTypeSerializer
                            .value()
                            .getConstructor()
                            .newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new JsonbException("Failed to serialize property [" + propertyName + "] of class [" + clazz + "]", e);
                }
            }
            properties.add(new Property(propertyName, nillable, method, serializer));
        }
        if (properties.isEmpty()) {
            throw new NoPropertiesToSerializeException(clazz);
        }
        return new ObjectSerializer(properties);
    }

    private final List<Property> properties;

    private ObjectSerializer(final List<Property> properties)
    {
        this.properties = properties;
    }

    @Override
    public void serialize(final Object obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.writeStartObject();
        for (final Property property : properties) {
            final String propertyName = property.name();
            try {
                final Object value = property.method().invoke(obj);
                if (!property.nillable() && isNull(value)) {
                    continue;
                }
                final JsonbSerializer<Object> serializer = property.serializer();
                if (serializer != null) {
                    generator.writeKey(propertyName);
                    serializer.serialize(value, generator, ctx);
                }
                else {
                    if (value instanceof TemporalAccessor temporalAccessor) {
                        final TemporalAccessorProperty temporalAccessorProperty =
                                buildTemporalAccessProperty(property.method(), temporalAccessor);
                        ctx.serialize(propertyName, temporalAccessorProperty, generator);
                    }
                    else {
                        ctx.serialize(propertyName, value, generator);
                    }
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new JsonbException("Failed to serialize property [" + propertyName + "] of class [" + obj.getClass() + "]",
                                         e);
            }
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
