package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

final class AdaptedJsonbDeserializer implements JsonbDeserializer<Object>
{
    private final JsonbDeserializer<?> adapted;
    private final JsonbAdapter<Object, Object> jsonbAdapter;
    private final Type type;

    AdaptedJsonbDeserializer(
            JsonbDeserializer<?> adapted,
            JsonbAdapter<Object, Object> jsonbAdapter,
            Type type)
    {
        this.adapted = adapted;
        this.jsonbAdapter = jsonbAdapter;
        this.type = type;
    }

    @Override
    public Object deserialize(
            JsonParser parser, DeserializationContext ctx, Type rtType)
    {
        Object obj = adapted.deserialize(parser, ctx, type);
        try {
            return jsonbAdapter.adaptFromJson(obj);
        }
        catch (Exception e) {
            throw new AdaptingFailedException(e);
        }
    }
}
