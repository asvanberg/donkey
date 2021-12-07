package io.github.asvanberg.donkey.test;

import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import java.io.StringReader;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;

class SerializationUtils {

    static JsonValueAssert assertParsedJson(final String json) {
        final JsonValue value = parseJson(json);
        return assertThat(value);
    }

    private static JsonValue parseJson(final String json) {
        final JsonProvider jsonp = JsonProvider.provider();
        try (var parser = jsonp.createParser(new StringReader(json)))
        {
            parser.next();
            return parser.getValue();
        }
    }
}
