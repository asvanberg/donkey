package io.github.asvanberg.donkey.test;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class EnumTest extends DefaultConfigurationTest
{
    enum Bool { TRUE, FALSE, FILE_NOT_FOUND }

    @Property
    public void serialize_enum_as_name_by_default(@ForAll Bool bool)
    {
        final String json = jsonb.toJson(bool);
        assertParsedJson(json)
                .isString()
                .isEqualTo(bool.name());
    }

    @Test
    public void deserialize_enum_uses_name_by_default() {
        final String json = """
                "FILE_NOT_FOUND"
                """;
        final Bool bool = jsonb.fromJson(json, Bool.class);
        assertThat(bool)
                .isEqualTo(Bool.FILE_NOT_FOUND);
    }
}
