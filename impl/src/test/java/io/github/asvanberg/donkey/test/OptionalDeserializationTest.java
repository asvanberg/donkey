package io.github.asvanberg.donkey.test;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

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

    @Property
    public void optional_long(@ForAll long l) {
        final String json = jsonb.toJson(OptionalLong.of(l));
        final OptionalLong optionalLong = jsonb.fromJson(json, OptionalLong.class);
        assertThat(optionalLong)
                .isPresent()
                .hasValue(l);
    }

    @Test
    public void empty_optional_long() {
        final String json = jsonb.toJson(OptionalLong.empty());
        final OptionalLong optionalLong = jsonb.fromJson(json, OptionalLong.class);
        assertThat(optionalLong)
                .isEmpty();
    }


    @Property
    public void optional_double(@ForAll double d) {
        final String json = jsonb.toJson(OptionalDouble.of(d));
        final OptionalDouble optionalDouble = jsonb.fromJson(json, OptionalDouble.class);
        assertThat(optionalDouble)
                .isPresent()
                .hasValue(d);
    }

    @Test
    public void empty_optional_double() {
        final String json = jsonb.toJson(OptionalDouble.empty());
        final OptionalDouble optionalDouble = jsonb.fromJson(json, OptionalDouble.class);
        assertThat(optionalDouble)
                .isEmpty();
    }
}
