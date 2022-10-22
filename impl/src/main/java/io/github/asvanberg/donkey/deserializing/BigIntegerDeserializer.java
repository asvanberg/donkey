package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.EnumSet;

public enum BigIntegerDeserializer implements JsonbDeserializer<BigInteger> {
    INSTANCE;

    @Override
    public BigInteger deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NUMBER -> parser.getBigDecimal().toBigIntegerExact();
            case VALUE_STRING -> new BigInteger(parser.getString());
            default -> throw new UnexpectedParserPositionException(
                    EnumSet.of(JsonParser.Event.VALUE_NUMBER, JsonParser.Event.VALUE_STRING), event);
        };
    }
}
