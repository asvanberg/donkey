package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.util.Optional;

class ObjectDeserializer<T> extends NullableDeserializer<T> {
    private final Creator<T> creator;

    static <T> ObjectDeserializer<T> of(Class<T> clazz) {
        return new ObjectDeserializer<>(Creator.forClass(clazz));
    }

    private ObjectDeserializer(final Creator<T> creator) {
        this.creator = creator;
    }

    @Override
    protected T getValue(final JsonParser parser, final DeserializationContext ctx) {
        Util.assertCurrentParserPosition(JsonParser.Event.START_OBJECT, parser);
        final Object[] creationParameters = new Object[creator.parameterCount()];
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
        return creator.create(creationParameters);
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
