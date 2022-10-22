package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

import java.util.EnumSet;
import java.util.Set;

public class UnexpectedParserPositionException extends JsonbException {
    public UnexpectedParserPositionException(final JsonParser.Event expected, final JsonParser.Event actual) {
        this(EnumSet.of(expected), actual);
    }

    public UnexpectedParserPositionException(final Set<JsonParser.Event> expected, final JsonParser.Event actual) {
        super("Unexpected parser position; expected " + expected + " but was [" + actual + "]");
    }
}
