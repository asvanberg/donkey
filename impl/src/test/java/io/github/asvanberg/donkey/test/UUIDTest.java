package io.github.asvanberg.donkey.test;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class UUIDTest extends DefaultConfigurationTest
{

    private static final String UUID_STRING = "a0d5bdee-a9cf-47c7-807d-8184cbeae0a9";

    @Test
    public void serialize_uuid()
    {
        final UUID uuid = UUID.fromString(UUID_STRING);
        final String json = jsonb.toJson(uuid);
        assertParsedJson(json)
                .isString()
                .isEqualTo(UUID_STRING);
    }

    @Test
    public void deserialize_uuid()
    {
        final String json = """
                "%s"
                """.formatted(UUID_STRING);
        final UUID uuid = jsonb.fromJson(json, UUID.class);
        assertThat(uuid)
                .asString()
                .isEqualTo(UUID_STRING);
    }
}
