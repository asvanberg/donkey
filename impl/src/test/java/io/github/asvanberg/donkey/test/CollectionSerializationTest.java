package io.github.asvanberg.donkey.test;

import jakarta.json.JsonString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;
import static io.github.asvanberg.donkey.test.SerializationUtils.*;

public class CollectionSerializationTest extends DefaultConfigurationTest {

    @Test
    public void list()
    {
        final List<String> list = List.of("foo", "bar");
        final String json = jsonb.toJson(list);
        assertParsedJson(json)
                .isArray()
                .extracting(JsonString.class::cast)
                .extracting(JsonString::getString)
                .containsExactlyElementsOf(List.of("foo", "bar"));
    }

    @Test
    public void map_of_strings()
    {
        final Map<String, String> map = Map.of("first_name", "Bob", "last_name", "Example");
        final String json = jsonb.toJson(map);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("first_name", property ->
                        assertThat(property).isString()
                                            .isEqualTo("Bob"))
                .hasEntrySatisfying("last_name", property ->
                        assertThat(property).isString()
                                            .isEqualTo("Example"));
    }
}
