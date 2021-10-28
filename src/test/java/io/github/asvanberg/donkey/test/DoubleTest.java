package io.github.asvanberg.donkey.test;

import jakarta.json.JsonNumber;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class DoubleTest extends DefaultConfigurationTest {
    @Property
    public void serialize_double(@ForAll double f) {
        final String json = jsonb.toJson(f);
        assertParsedJson(json)
                .isNumber()
                .extracting(JsonNumber::doubleValue)
                .isEqualTo(f);
    }

    @Property
    public void deserialize_double(@ForAll double f) {
        final String json = Double.toString(f);
        final double deserialized = jsonb.fromJson(json, double.class);
        assertThat(deserialized)
                .isEqualTo(f);
    }

    @Property
    public void back_and_forth(@ForAll double f) {
        final String json = jsonb.toJson(f);
        final double backAndForth = jsonb.fromJson(json, double.class);
        assertThat(backAndForth)
                .isEqualTo(f);
    }
}
