package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.util.OptionalInt;

class OptionalIntDeserializer extends NullableDeserializer<OptionalInt> {
    OptionalIntDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    protected OptionalInt getValue(
            final JsonParser parser, final DeserializationContext ctx)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return OptionalInt.of(parser.getInt());
    }

    @Override
    protected OptionalInt nullValue() {
        return OptionalInt.empty();
    }
}
