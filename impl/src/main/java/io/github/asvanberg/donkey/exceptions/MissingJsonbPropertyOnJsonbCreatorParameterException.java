package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class MissingJsonbPropertyOnJsonbCreatorParameterException extends JsonbException {
    public MissingJsonbPropertyOnJsonbCreatorParameterException(
            final Parameter parameter,
            final Executable executable)
    {
        super("JsonbProperty annotation missing on parameter [" + parameter + "] to JsonbCreator [" + executable + "]");
    }
}
