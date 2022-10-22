package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

public enum IntegerCodec implements JsonbCodec<Integer> {
    INSTANCE;

    @Override
    public void serialize(final Integer obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER, parser);
        return parser.getInt();
    }
}
