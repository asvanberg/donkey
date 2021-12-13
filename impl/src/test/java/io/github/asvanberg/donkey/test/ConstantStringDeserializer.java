package io.github.asvanberg.donkey.test;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

public class ConstantStringDeserializer implements JsonbDeserializer<String>
{
    private final String constantString;

    public ConstantStringDeserializer(String constantString)
    {
        this.constantString = constantString;
    }

    @Override
    public String deserialize(
            JsonParser parser, DeserializationContext ctx, Type rtType)
    {
        return constantString;
    }
}
