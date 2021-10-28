package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.util.OptionalLong;

class OptionalLongDeserializer extends NullableDeserializer<OptionalLong> {
    OptionalLongDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    protected OptionalLong getValue(
            final JsonParser parser, final DeserializationContext ctx)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return OptionalLong.of(parser.getLong());
    }

    @Override
    protected OptionalLong nullValue() {
        return OptionalLong.empty();
    }
}
