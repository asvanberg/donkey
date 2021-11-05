package io.github.asvanberg.donkey.internal;

import jakarta.json.bind.adapter.JsonbAdapter;

import java.net.URI;
import java.net.URISyntaxException;

public enum URIStringJsonbAdapter implements JsonbAdapter<URI, String>
{
    INSTANCE;

    @Override
    public String adaptToJson(final URI obj)
    {
        return obj.toString();
    }

    @Override
    public URI adaptFromJson(final String obj)
            throws URISyntaxException
    {
        return new URI(obj);
    }
}
