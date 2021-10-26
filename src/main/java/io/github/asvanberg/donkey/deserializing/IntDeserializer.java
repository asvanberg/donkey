package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

class IntDeserializer extends BaseDeserializer<Integer>
{
    IntDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public Integer deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return parser.getInt();
    }
}
