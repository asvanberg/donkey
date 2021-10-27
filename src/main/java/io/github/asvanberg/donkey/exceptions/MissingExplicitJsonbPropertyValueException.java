package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class MissingExplicitJsonbPropertyValueException extends JsonbException {
    public MissingExplicitJsonbPropertyValueException(final Parameter parameter, final Executable executable) {
        super("JsonbProperty annotation on parameter [" + parameter + "] to JsonbCreator [" + executable + "] is missing value() defining the JSON attribute name");
    }
}
