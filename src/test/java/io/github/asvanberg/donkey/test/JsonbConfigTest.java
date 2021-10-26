package io.github.asvanberg.donkey.test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.spi.JsonbProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonbConfigTest {
    private static final String PREFIX = "prefix";

    @Test
    public void user_provided_serializers_are_prioritised()
            throws Exception
    {
        try (Jsonb jsonb = initDonkey()) {
            final String json = jsonb.toJson("simple");
            assertParsedJson(json, jsonValue ->
                    jsonValue.isString()
                             .startsWith(PREFIX));
        }
    }

    private static Jsonb initDonkey() {
        final JsonbConfig config = new JsonbConfig();
        config.withSerializers(new PrefixingStringSerializer(PREFIX));
        return JsonbProvider.provider("io.github.asvanberg.donkey.DonkeyProvider")
                            .create()
                            .withConfig(config)
                            .build();
    }
}
