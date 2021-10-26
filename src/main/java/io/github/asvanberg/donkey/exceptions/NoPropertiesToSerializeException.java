package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

public class NoPropertiesToSerializeException extends JsonbException {
    public NoPropertiesToSerializeException(final Class<?> clazz) {
        super("No properties to serialize found for class [" + clazz.getName() + "]");
    }
}
