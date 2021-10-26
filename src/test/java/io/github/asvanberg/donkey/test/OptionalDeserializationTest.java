package io.github.asvanberg.donkey.test;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalDeserializationTest extends DefaultConfigurationTest {
    @Test
    public void optional_string_deserialization_null() {
        Optional<String> optional = jsonb.fromJson("null", new Generic<Optional<String>>(){}.type());

        assertThat(optional).isEmpty();
    }

    @Test
    public void optional_string_deserialization_empty_string() {
        Optional<String> optional = jsonb.fromJson("\"\"", new Generic<Optional<String>>(){}.type());

        assertThat(optional).contains("");
    }
}
