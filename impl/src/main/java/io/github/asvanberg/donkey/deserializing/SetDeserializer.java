package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.JsonbDeserializer;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

class SetDeserializer<T> extends CollectionDeserializer<T> {
    SetDeserializer(Type[] parameters) {
        super(parameters[0]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static JsonbDeserializer<?> of(Type[] types) {
        if (types[0] instanceof Class<?> clazz && Enum.class.isAssignableFrom(clazz)) {
            return new EnumSetDeserializer(clazz);
        }
        return new SetDeserializer<>(types);
    }

    @Override
    Collection<T> newCollection() {
        return new HashSet<>();
    }

    private static class EnumSetDeserializer<T extends Enum<T>> extends CollectionDeserializer<T> {
        private final Class<T> clazz;

        public EnumSetDeserializer(Class<T> clazz) {
            super(clazz);
            this.clazz = clazz;
        }

        @Override
        Collection<T> newCollection() {
            return EnumSet.noneOf(clazz);
        }
    }
}
