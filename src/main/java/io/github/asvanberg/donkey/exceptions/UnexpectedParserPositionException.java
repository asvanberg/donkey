package io.github.asvanberg.donkey.exceptions;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;

public class UnexpectedParserPositionException extends JsonbException {
    public UnexpectedParserPositionException(final JsonParser.Event expected, final JsonParser.Event actual) {
        super("Unexpected parser position; expected [" + expected + "] but was [" + actual + "]");
    }
}
