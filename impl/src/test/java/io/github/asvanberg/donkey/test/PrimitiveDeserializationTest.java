package io.github.asvanberg.donkey.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveDeserializationTest extends DefaultConfigurationTest
{
    @Test
    public void integer()
    {
        final int i = 42;
        final Integer value = jsonb.fromJson(Integer.toString(i), int.class);
        assertThat(value)
                .isEqualTo(i);
    }

    @Test
    public void long_() {
        String json = """
                42
                """;

        final long value = jsonb.fromJson(json, long.class);
        assertThat(value).isEqualTo(42);
    }
}
