package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

public class NoJsonbCreatorException extends JsonbException {
    public NoJsonbCreatorException(final Class<?> clazz) {
        super("No creator for [" + clazz + "]");
    }
}
