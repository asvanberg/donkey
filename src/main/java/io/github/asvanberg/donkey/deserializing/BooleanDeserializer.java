package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

class BooleanDeserializer extends BaseDeserializer<Boolean> {
    protected BooleanDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public Boolean deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        return switch (parserHistory.currentEvent()) {
            case VALUE_TRUE -> true;
            case VALUE_FALSE -> false;
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_TRUE, parserHistory.currentEvent());
        };
    }
}
