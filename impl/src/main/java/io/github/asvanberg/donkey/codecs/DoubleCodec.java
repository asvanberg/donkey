package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

public enum DoubleCodec implements JsonbCodec<Double> {
    INSTANCE;

    @Override
    public void serialize(final Double obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }

    @Override
    public Double deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER, parser);
        return parser.getBigDecimal().doubleValue();
    }
}
