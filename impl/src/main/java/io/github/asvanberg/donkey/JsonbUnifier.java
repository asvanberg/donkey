package io.github.asvanberg.donkey;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

abstract class JsonbUnifier implements Jsonb
{
    @Override
    public <T> T fromJson(final String str, final Class<T> type)
            throws JsonbException
    {
        return fromJson(new StringReader(str), type);
    }

    @Override
    public <T> T fromJson(final String str, final Type runtimeType)
            throws JsonbException
    {
        return fromJson(new StringReader(str), runtimeType);
    }

    @Override
    public <T> T fromJson(final InputStream stream, final Class<T> type)
            throws JsonbException
    {
        return fromJson(new InputStreamReader(stream), type);
    }

    @Override
    public <T> T fromJson(final InputStream stream, final Type runtimeType)
            throws JsonbException
    {
        return fromJson(new InputStreamReader(stream), runtimeType);
    }

    @Override
    public <T> T fromJson(final Reader reader, final Class<T> type)
            throws JsonbException
    {
        return fromJson(reader, (Type) type);
    }

    @Override
    public String toJson(final Object object, final Type runtimeType)
            throws JsonbException
    {
        return toJson(object);
    }

    @Override
    public String toJson(final Object object)
            throws JsonbException
    {
        final StringWriter writer = new StringWriter();
        toJson(object, writer);
        return writer.toString();
    }

    @Override
    public void toJson(final Object object, final Type runtimeType, final Writer writer)
            throws JsonbException
    {
        toJson(object, writer);
    }

    @Override
    public void toJson(final Object object, final OutputStream stream)
            throws JsonbException
    {
        toJson(object, new OutputStreamWriter(stream, StandardCharsets.UTF_8));
    }

    @Override
    public void toJson(final Object object, final Type runtimeType, final OutputStream stream)
            throws JsonbException
    {
        toJson(object, new OutputStreamWriter(stream, StandardCharsets.UTF_8));
    }
}
