package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.util.OptionalDouble;
import java.util.OptionalInt;

class OptionalDoubleDeserializer extends NullableDeserializer<OptionalDouble> {
    OptionalDoubleDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    protected OptionalDouble getValue(
            final JsonParser parser, final DeserializationContext ctx)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER);
        return OptionalDouble.of(parser.getBigDecimal().doubleValue());
    }

    @Override
    protected OptionalDouble nullValue() {
        return OptionalDouble.empty();
    }
}
