package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

enum FloatSerializer implements JsonbSerializer<Float> {
    INSTANCE;

    @Override
    public void serialize(
            final Float obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(obj);
    }
}
