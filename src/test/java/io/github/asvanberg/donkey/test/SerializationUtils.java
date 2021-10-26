package io.github.asvanberg.donkey.test;

import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;

import java.io.StringReader;
import java.util.function.Consumer;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;

class SerializationUtils {
    static void assertParsedJson(final String json, final Consumer<JsonValueAssert> assertJson)
    {
        final JsonProvider jsonp = JsonProvider.provider();
        try (var parser = jsonp.createParser(new StringReader(json)))
        {
            parser.next();
            final JsonValue value = parser.getValue();
            assertJson.accept(assertThat(value));
        }
    }
}
