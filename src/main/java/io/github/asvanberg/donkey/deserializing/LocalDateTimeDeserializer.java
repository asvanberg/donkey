package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

class LocalDateTimeDeserializer extends BaseDeserializer<LocalDateTime> {
    LocalDateTimeDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public LocalDateTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return LocalDateTime.parse(parser.getString());
    }
}
