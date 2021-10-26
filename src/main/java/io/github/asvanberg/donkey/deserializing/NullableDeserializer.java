package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

abstract class NullableDeserializer<T> extends BaseDeserializer<T> {

    NullableDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public final T deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        if (parserHistory.currentEvent() == JsonParser.Event.VALUE_NULL) {
            return nullValue();
        }
        else {
            return getValue(parser, ctx);
        }
    }

    protected abstract T getValue(JsonParser parser, DeserializationContext ctx);

    protected T nullValue() {
        return null;
    }
}
