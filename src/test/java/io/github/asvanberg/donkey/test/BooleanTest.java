package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BooleanTest extends DefaultConfigurationTest {
    @Test
    public void deserialize_true() {
        final String json = "true";
        final boolean bool = jsonb.fromJson(json, boolean.class);
        assertThat(bool)
                .isTrue();
    }

    @Test
    public void deserialize_false() {
        final String json = "false";
        final boolean bool = jsonb.fromJson(json, boolean.class);
        assertThat(bool)
                .isFalse();
    }

    @Test
    public void fails_on_wrong_json_value_type() {
        try {
            final String json = "1";
            jsonb.fromJson(json, boolean.class);
            fail("Should error out on wrong json value type");
        } catch (UnexpectedParserPositionException expected) {}
    }
}
