package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.OptionalDouble;

public enum OptionalDoubleCodec implements JsonbCodec<OptionalDouble> {
    INSTANCE;

    @Override
    public void serialize(final OptionalDouble obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsDouble());
        }
        else {
            generator.writeNull();
        }
    }

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
