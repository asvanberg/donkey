package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;

record RegisteredSerializer(Priority priority, Class<?> clazz, JsonbSerializer<?> serializer) {
    enum Priority {
        // Lowest to highest
        BUILT_IN,
        CONFIGURED
    }

    boolean supports(final Class<?> runtimeType) {
        return clazz.isAssignableFrom(runtimeType);
    }
}
