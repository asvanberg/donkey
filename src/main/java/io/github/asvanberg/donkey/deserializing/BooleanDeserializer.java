package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

enum BooleanDeserializer implements JsonbDeserializer<Boolean> {
    INSTANCE;

    @Override
    public Boolean deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_TRUE -> true;
            case VALUE_FALSE -> false;
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_TRUE, event);
        };
    }
}
