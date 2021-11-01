package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.OptionalInt;

enum OptionalIntDeserializer implements JsonbDeserializer<OptionalInt> {
    INSTANCE;

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
