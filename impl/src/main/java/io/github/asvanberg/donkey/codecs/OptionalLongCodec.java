package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.OptionalLong;

public enum OptionalLongCodec implements JsonbCodec<OptionalLong> {
    INSTANCE;

    @Override
    public void serialize(final OptionalLong obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsLong());
        }
        else {
            generator.writeNull();
        }
    }

    @Override
    public final OptionalLong deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NULL -> OptionalLong.empty();
            case VALUE_NUMBER -> OptionalLong.of(parser.getLong());
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_NUMBER, event);
        };
    }
}
