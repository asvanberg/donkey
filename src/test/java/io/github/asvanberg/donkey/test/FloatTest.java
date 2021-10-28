package io.github.asvanberg.donkey.test;

import jakarta.json.JsonNumber;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.math.BigDecimal;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class FloatTest extends DefaultConfigurationTest {
    @Property
    public void serialize_float(@ForAll float f) {
        final String json = jsonb.toJson(f);
        assertParsedJson(json)
                .isNumber()
                .extracting(JsonNumber::bigDecimalValue)
                .extracting(BigDecimal::floatValue)
                .isEqualTo(f);
    }

    @Property
    public void deserialize_float(@ForAll float f) {
        final String json = Float.toString(f);
        final float deserialized = jsonb.fromJson(json, float.class);
        assertThat(deserialized)
                .isEqualTo(f);
    }

    @Property
    public void back_and_forth(@ForAll float f) {
        final String json = jsonb.toJson(f);
        final float backAndForth = jsonb.fromJson(json, float.class);
        assertThat(backAndForth)
                .isEqualTo(f);
    }
}
