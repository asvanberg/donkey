package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

public enum BooleanCodec implements JsonbCodec<Boolean> {
    INSTANCE;

    @Override
    public void serialize(final Boolean obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }

    @Override
    public Boolean deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_TRUE -> true;
            case VALUE_FALSE -> false;
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_TRUE, event);
        };
    }
}
