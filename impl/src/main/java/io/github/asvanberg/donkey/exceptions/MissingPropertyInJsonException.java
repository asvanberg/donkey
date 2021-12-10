package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

public class MissingPropertyInJsonException extends JsonbException
{
    public MissingPropertyInJsonException(final String propertyName)
    {
        super("Property [" + propertyName + "] missing from JSON");
    }
}
