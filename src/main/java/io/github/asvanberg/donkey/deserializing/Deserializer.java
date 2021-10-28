package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Deserializer {
    private final Map<Class<?>, Function<ParserHistory, JsonbDeserializer<?>>> deserializers
            = new HashMap<>();
    private final Map<Class<?>, BiFunction<ParserHistory, Type[], JsonbDeserializer<?>>> parameterizedDeserializers
            = new HashMap<>();

    public Deserializer(final JsonbConfig config) {
        deserializers.put(Integer.class, IntDeserializer::new);
        deserializers.put(int.class, IntDeserializer::new);
        deserializers.put(Float.class, FloatDeserializer::new);
        deserializers.put(float.class, FloatDeserializer::new);
        deserializers.put(Double.class, DoubleDeserializer::new);
        deserializers.put(double.class, DoubleDeserializer::new);
        deserializers.put(String.class, StringDeserializer::new);
        deserializers.put(long.class, LongDeserializer::new);
        deserializers.put(boolean.class, BooleanDeserializer::new);
        deserializers.put(Boolean.class, BooleanDeserializer::new);
        parameterizedDeserializers.put(ArrayList.class, ignoringHistory(ListDeserializer::new));
        parameterizedDeserializers.put(HashMap.class, ignoringHistory(MapDeserializer::new));
        parameterizedDeserializers.put(Optional.class, OptionalDeserializer::new);
    }

    private BiFunction<ParserHistory, Type[], JsonbDeserializer<?>> ignoringHistory(
            Function<Type[], JsonbDeserializer<?>> deserializer)
    {
        return (parserHistory, types) -> deserializer.apply(types);
    }

    @SuppressWarnings("unchecked")
    private <T> JsonbDeserializer<T> getJsonbDeserializer(
            final DeserializationProcess deserializationProcess,
            final Type runtimeType)
    {
        return (JsonbDeserializer<T>) getUncheckedJsonbDeserializer(deserializationProcess, runtimeType);
    }

    private JsonbDeserializer<?> getUncheckedJsonbDeserializer(
            final DeserializationProcess deserializationProcess,
            final Type runtimeType)
    {
        if (runtimeType instanceof Class<?> clazz) {
            return deserializers.computeIfAbsent(clazz, ObjectDeserializerFactory::new)
                                .apply(deserializationProcess);
        }
        else if (runtimeType instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType() instanceof Class<?> clazz) {
                for (var entry : parameterizedDeserializers.entrySet()) {
                    if (clazz.isAssignableFrom(entry.getKey())) {
                        return entry.getValue()
                                    .apply(deserializationProcess, parameterizedType.getActualTypeArguments());
                    }
                }
            }
        }
        throw new JsonbException(runtimeType + " not supported");
    }

    public <T> T deserialize(final JsonParser parser, final Type runtimeType) {
        return new DeserializationProcess(parser).deserialize(runtimeType);
    }

    private class DeserializationProcess implements DeserializationContext, ParserHistory {
        private final JsonParser parser;
        private JsonParser.Event currentEvent;

        public DeserializationProcess(final JsonParser parser) {
            this.parser = new DelegatingJsonParser(parser) {
                @Override
                public Event next() {
                    return currentEvent = super.next();
                }
            };
            this.parser.next();
        }

        public <T> T deserialize(final Type type) {
            return deserialize(type, parser);
        }

        @Override
        public <T> T deserialize(final Class<T> clazz, final JsonParser parser) {
            return deserialize((Type) clazz, parser);
        }

        @Override
        public <T> T deserialize(final Type type, final JsonParser parser) {
            final JsonbDeserializer<T> jsonbDeserializer = getJsonbDeserializer(this, type);
            return jsonbDeserializer.deserialize(parser, this, type);
        }

        @Override
        public JsonParser.Event currentEvent() {
            return currentEvent;
        }
    }
}
