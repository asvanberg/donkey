package io.github.asvanberg.donkey;

import io.github.asvanberg.donkey.deserializing.Deserializer;
import io.github.asvanberg.donkey.exceptions.InternalProcessingException;
import io.github.asvanberg.donkey.serializing.Serializers;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

class Donkey extends JsonbUnifier implements SerializationContext {
    private final JsonProvider provider;
    private final Serializers serializers;
    private final Deserializer deserializer;

    Donkey(
            final JsonProvider provider,
            final Serializers serializers,
            final Deserializer deserializer)
    {
        this.provider = provider;
        this.serializers = serializers;
        this.deserializer = deserializer;
    }

    @Override
    public void close()
    {
    }

    @Override
    public void toJson(final Object object, final Writer writer)
            throws JsonbException
    {
        try (final JsonGenerator generator = provider.createGenerator(writer))
        {
            serialize(object, generator);
        }
        catch (JsonbException jsonbException)
        {
            throw jsonbException;
        }
        catch (RuntimeException exception)
        {
            throw new InternalProcessingException(exception);
        }
    }

    @Override
    public <T> void serialize(final T object, final JsonGenerator generator)
    {
        if (object != null)
        {
            final JsonbSerializer<Object> serializer = serializers.getJsonbSerializer(object.getClass());
            serializer.serialize(object, generator, this);
        }
        else
        {
            generator.writeNull();
        }
    }

    @Override
    public <T> void serialize(final String key, final T object, final JsonGenerator generator)
    {
        generator.writeKey(key);
        serialize(object, generator);
    }

    @Override
    public <T> T fromJson(final Reader reader, final Type runtimeType)
            throws JsonbException
    {
        try (var parser = provider.createParser(reader))
        {
            return deserializer.deserialize(parser, runtimeType);
        }
        catch (JsonbException jsonbException)
        {
            throw jsonbException;
        }
        catch (RuntimeException exception)
        {
            throw new InternalProcessingException(exception);
        }
    }
}
