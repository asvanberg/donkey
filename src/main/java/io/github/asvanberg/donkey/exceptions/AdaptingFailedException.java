package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

public class AdaptingFailedException extends JsonbException {
    public AdaptingFailedException(final Throwable cause) {
        super(null, cause);
    }
}
