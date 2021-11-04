package io.github.asvanberg.donkey.internal;

import jakarta.json.bind.adapter.JsonbAdapter;

public enum NullAdapter implements JsonbAdapter<Object, Object>
{
    INSTANCE;

    @Override
    public Object adaptToJson(final Object obj)
    {
        return obj;
    }

    @Override
    public Object adaptFromJson(final Object obj)
    {
        return obj;
    }
}
