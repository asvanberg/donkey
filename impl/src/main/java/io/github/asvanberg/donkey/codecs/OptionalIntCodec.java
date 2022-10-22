package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.OptionalInt;

public enum OptionalIntCodec implements JsonbCodec<OptionalInt> {
    INSTANCE;

    @Override
    public void serialize(final OptionalInt obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsInt());
        }
        else {
            generator.writeNull();
        }
    }

    @Override
    public final OptionalInt deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        return switch (event) {
            case VALUE_NULL -> OptionalInt.empty();
            case VALUE_NUMBER -> OptionalInt.of(parser.getInt());
            default -> throw new UnexpectedParserPositionException(JsonParser.Event.VALUE_NUMBER, event);
        };
    }
}
