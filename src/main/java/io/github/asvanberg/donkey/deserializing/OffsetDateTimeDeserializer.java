package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;

class OffsetDateTimeDeserializer extends BaseDeserializer<OffsetDateTime> {
    OffsetDateTimeDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public OffsetDateTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return OffsetDateTime.parse(parser.getString());
    }
}
