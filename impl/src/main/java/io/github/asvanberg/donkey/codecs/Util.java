package io.github.asvanberg.donkey.codecs;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.stream.JsonParser;

public class Util {
    static void assertCurrentParserPosition(
            final JsonParser.Event expected,
            final JsonParser parser)
    {
        final JsonParser.Event event = parser.next();
        if (event != expected) {
            throw new UnexpectedParserPositionException(expected, event);
        }
    }
}
