package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.Instant;

class InstantDeserializer extends BaseDeserializer<Instant> {
    InstantDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public Instant deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return Instant.parse(parser.getString());
    }
}
