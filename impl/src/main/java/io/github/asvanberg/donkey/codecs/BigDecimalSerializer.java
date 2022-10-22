package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.math.BigDecimal;

public enum BigDecimalSerializer implements JsonbSerializer<BigDecimal> {
    INSTANCE;

    @Override
    public void serialize(final BigDecimal obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}
