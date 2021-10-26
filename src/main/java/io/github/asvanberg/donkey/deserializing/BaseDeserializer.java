package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

abstract class BaseDeserializer<T> implements JsonbDeserializer<T> {
    protected final ParserHistory parserHistory;

    protected BaseDeserializer(final ParserHistory parserHistory) {
        this.parserHistory = parserHistory;
    }

    protected void assertCurrentParserPosition(final JsonParser.Event expected) {
        if (parserHistory.currentEvent() != expected) {
            throw new UnexpectedParserPositionException(expected, parserHistory.currentEvent());
        }
    }
}
