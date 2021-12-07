package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ListDeserializer<T> implements JsonbDeserializer<List<T>>
{
    private final Type elementType;

    ListDeserializer(final Type[] parameters)
    {
        this.elementType = parameters[0];
    }

    @Override
    public List<T> deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final PeekableJsonParser peekableJsonParser = new PeekableJsonParser(parser);
        peekableJsonParser.next();
        final ArrayList<T> list = new ArrayList<>();
        while (peekableJsonParser.peek() != JsonParser.Event.END_ARRAY) {
            list.add(ctx.deserialize(elementType, peekableJsonParser));
        }
        return list;
    }
}
