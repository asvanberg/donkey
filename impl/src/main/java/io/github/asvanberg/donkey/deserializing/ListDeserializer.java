package io.github.asvanberg.donkey.deserializing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

class ListDeserializer<T> extends CollectionDeserializer<T>
{
    ListDeserializer(final Type[] parameters)
    {
        super(parameters[0]);
    }

    @Override
    protected Collection<T> newCollection() {
        return new ArrayList<>();
    }
}
