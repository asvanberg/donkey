package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

abstract class NullableDeserializer<T> implements JsonbDeserializer<T> {
    @Override
    public final T deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final JsonParser.Event event = parser.next();
        if (event == JsonParser.Event.VALUE_NULL) {
            return nullValue();
        }
        else {
            final PeekableJsonParser peekableJsonParser = new PeekableJsonParser(parser, event);
            return getValue(peekableJsonParser, ctx);
        }
    }

    protected abstract T getValue(JsonParser parser, DeserializationContext ctx);

    protected T nullValue() {
        return null;
    }
}
