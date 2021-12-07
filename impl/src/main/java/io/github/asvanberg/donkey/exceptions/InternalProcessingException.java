package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

public class InternalProcessingException extends JsonbException
{
    public InternalProcessingException(final Throwable cause)
    {
        super(null, cause);
    }
}
