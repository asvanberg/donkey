package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

class LongDeserializer extends BaseDeserializer<Long> {
    LongDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public Long deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return parser.getLong();
    }
}
