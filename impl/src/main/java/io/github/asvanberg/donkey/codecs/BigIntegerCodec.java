package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.EnumSet;

public enum BigIntegerCodec implements JsonbCodec<BigInteger> {
    INSTANCE;

    @Override
    public void serialize(final BigInteger obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }

    @Override
    public BigInteger deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NUMBER -> parser.getBigDecimal().toBigIntegerExact();
            case VALUE_STRING -> new BigInteger(parser.getString());
            default -> throw new UnexpectedParserPositionException(
                    EnumSet.of(JsonParser.Event.VALUE_NUMBER, JsonParser.Event.VALUE_STRING), event);
        };
    }
}
