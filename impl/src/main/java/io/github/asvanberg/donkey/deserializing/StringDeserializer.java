package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

enum StringDeserializer implements JsonbDeserializer<String> {
    INSTANCE;

    @Override
    public final String deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NULL -> null;
            case VALUE_STRING -> parser.getString();
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_STRING, event);
        };
    }
}
