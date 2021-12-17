package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.MissingPropertyInJsonException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

class ObjectDeserializer<T> extends NullableDeserializer<T> {
    private static final Object MISSING_FROM_JSON = new Object();

    private final Creator<T> creator;

    @SuppressWarnings("unchecked")
    static <T> JsonbDeserializer<T> of(Class<T> clazz) {
        // check if there's an apt generated serializer
        try {
            final String deserializerClassName = clazz.getName() + "$donkey$apt$deserializer";
            return (JsonbDeserializer<T>) Class.forName(deserializerClassName)
                                                    .getConstructor().newInstance();
        } catch (Exception ignored) {}
        return new ObjectDeserializer<>(Creator.forClass(clazz));
    }

    private ObjectDeserializer(final Creator<T> creator) {
        this.creator = creator;
    }

    @Override
    protected T getValue(final JsonParser parser, final DeserializationContext ctx) {
        Util.assertCurrentParserPosition(JsonParser.Event.START_OBJECT, parser);
        final Object[] creationParameters = new Object[creator.parameterCount()];
        Arrays.fill(creationParameters, MISSING_FROM_JSON);
        while (parser.next() != JsonParser.Event.END_OBJECT) {
            final String parameterName = parser.getString();
            final Optional<Creator.Parameter> parameter = creator.getParameterByName(parameterName);
            if (parameter.isEmpty()) {
                skipAttribute(parser);
            }
            else {
                final Creator.Parameter param = parameter.get();
                final Object parameterValue = ctx.deserialize(param.type(), parser);
                creationParameters[param.index()] = parameterValue;
            }
        }
        for (Creator.Parameter parameter : creator.parameters()) {
            if (creationParameters[parameter.index()] == MISSING_FROM_JSON) {
                Object replacementValue = getReplacementValue(parameter.type());
                if (replacementValue != null) {
                    creationParameters[parameter.index()] = replacementValue;
                }
                else {
                    throw new MissingPropertyInJsonException(parameter.name());
                }
            }
        }
        return creator.create(creationParameters);
    }

    private Object getReplacementValue(final Type type)
    {
        if (type instanceof ParameterizedType pt && pt.getRawType() == Optional.class) {
            return Optional.empty();
        }
        else if (type == OptionalInt.class) {
            return OptionalInt.empty();
        }
        else if (type == OptionalLong.class) {
            return OptionalLong.empty();
        }
        else if (type == OptionalDouble.class) {
            return OptionalDouble.empty();
        }
        else {
            return null;
        }
    }

    private void skipAttribute(final JsonParser parser) {
        switch (parser.next()) {
            case START_ARRAY -> parser.skipArray();
            case START_OBJECT -> parser.skipObject();
            default -> {
            }
        }
    }
}
