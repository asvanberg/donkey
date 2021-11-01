package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.OptionalDouble;

enum OptionalDoubleDeserializer implements JsonbDeserializer<OptionalDouble> {
    INSTANCE;

    @Override
    public final OptionalDouble deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NULL -> OptionalDouble.empty();
            case VALUE_NUMBER -> OptionalDouble.of(parser.getBigDecimal().doubleValue());
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_NUMBER, event);
        };
    }
}
