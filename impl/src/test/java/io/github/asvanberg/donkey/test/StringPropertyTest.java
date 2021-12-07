package io.github.asvanberg.donkey.test;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.WithNull;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringPropertyTest extends DefaultConfigurationTest {
    @Property
    public void all_string_serialize_and_deserialize_back(
            @ForAll @WithNull String someString)
    {
        final String json = jsonb.toJson(someString);
        final String deserialized = jsonb.fromJson(json, String.class);

        assertEquals(someString, deserialized);
    }
}
