package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public enum BigDecimalCodec implements JsonbSerializer<BigDecimal>, JsonbDeserializer<BigDecimal> {
    INSTANCE;

    @Override
    public void serialize(final BigDecimal obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }

    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NUMBER -> parser.getBigDecimal();
            case VALUE_STRING -> new BigDecimal(parser.getString());
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_NUMBER, event);
        };
    }
}
