package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

class FloatDeserializer extends BaseDeserializer<Float> {
    FloatDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public Float deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return parser.getBigDecimal().floatValue();
    }
}
