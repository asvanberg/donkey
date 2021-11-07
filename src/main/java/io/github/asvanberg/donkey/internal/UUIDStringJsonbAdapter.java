package io.github.asvanberg.donkey.internal;

import jakarta.json.bind.adapter.JsonbAdapter;

import java.util.UUID;

public enum UUIDStringJsonbAdapter implements JsonbAdapter<UUID, String>
{
    INSTANCE;

    @Override
    public String adaptToJson(final UUID uuid)
    {
        return uuid.toString();
    }

    @Override
    public UUID adaptFromJson(final String obj)
    {
        return UUID.fromString(obj);
    }
}
