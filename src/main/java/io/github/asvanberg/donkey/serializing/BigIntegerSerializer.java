package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.math.BigInteger;

public enum BigIntegerSerializer implements JsonbSerializer<BigInteger> {
    INSTANCE;

    @Override
    public void serialize(final BigInteger obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}
