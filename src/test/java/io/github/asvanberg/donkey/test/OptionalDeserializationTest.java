package io.github.asvanberg.donkey.test;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalInt;

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

    @Property
    public void optional_int(@ForAll int i) {
        final String json = jsonb.toJson(OptionalInt.of(i));
        final OptionalInt optionalInt = jsonb.fromJson(json, OptionalInt.class);
        assertThat(optionalInt)
                .isPresent()
                .hasValue(i);
    }

    @Test
    public void empty_optional_int() {
        final String json = jsonb.toJson(OptionalInt.empty());
        final OptionalInt optionalInt = jsonb.fromJson(json, OptionalInt.class);
        assertThat(optionalInt)
                .isEmpty();
    }
}
