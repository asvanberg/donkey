package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

public enum FloatCodec implements JsonbCodec<Float> {
    INSTANCE;

    @Override
    public void serialize(
            final Float obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(obj);
    }

    @Override
    public Float deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER, parser);
        return parser.getBigDecimal().floatValue();
    }
}
