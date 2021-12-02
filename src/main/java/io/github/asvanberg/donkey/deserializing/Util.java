package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.InternalProcessingException;
import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.stream.JsonParser;

import java.util.concurrent.Callable;

class Util {
    static void assertCurrentParserPosition(final JsonParser.Event expected, final JsonParser parser) {
        final JsonParser.Event event = parser.next();
        if (event != expected) {
            throw new UnexpectedParserPositionException(expected, event);
        }
    }

    static <A> A throwing(Callable<A> f)
    {
        try {
            return f.call();
        }
        catch (Exception e) {
            throw new InternalProcessingException(e);
        }
    }
}
