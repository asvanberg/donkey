package io.github.asvanberg.donkey.test;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public class PrefixingStringSerializer implements JsonbSerializer<String> {
    private final String prefix;

    public PrefixingStringSerializer(final String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void serialize(final String obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(prefixValue(prefix, obj));
    }

    public static String prefixValue(final String prefix, final String obj) {
        return "%s-%s".formatted(prefix, obj);
    }
}
