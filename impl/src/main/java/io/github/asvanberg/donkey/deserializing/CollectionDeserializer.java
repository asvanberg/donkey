package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.Collection;

abstract class CollectionDeserializer<T> implements JsonbDeserializer<Collection<T>>
{
    protected final Type elementType;

    CollectionDeserializer(final Type elementType) {
        this.elementType = elementType;
    }

    @Override
    public Collection<T> deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final PeekableJsonParser peekableJsonParser = new PeekableJsonParser(parser);
        peekableJsonParser.next();
        final Collection<T> list = newCollection();
        while (peekableJsonParser.peek() != JsonParser.Event.END_ARRAY) {
            list.add(ctx.deserialize(elementType, peekableJsonParser));
        }
        return list;
    }

    abstract Collection<T> newCollection();
}
