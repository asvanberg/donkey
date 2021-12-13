package io.github.asvanberg.donkey.test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.spi.JsonbProvider;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonbConfigTest {
    private static final String PREFIX = "prefix";
    private static final String CONSTANT_STRING = "a-string";

    @Test
    public void user_provided_serializers_are_prioritised()
            throws Exception
    {
        try (Jsonb jsonb = initDonkey()) {
            final String json = jsonb.toJson("simple");
            assertParsedJson(json)
                    .isString()
                    .startsWith(PREFIX);
        }
    }

    @Test
    public void user_provided_deserializers_are_prioritised()
            throws Exception
    {
        try (Jsonb jsonb = initDonkey()) {
            String json = """
                    "my string"
                    """;
            String str = jsonb.fromJson(json, String.class);
            assertThat(str)
                    .isEqualTo(CONSTANT_STRING);
        }
    }

    private static Jsonb initDonkey() {
        final JsonbConfig config = new JsonbConfig();
        config.withSerializers(new PrefixingStringSerializer(PREFIX));
        config.withDeserializers(new ConstantStringDeserializer(CONSTANT_STRING));
        return JsonbProvider.provider("io.github.asvanberg.donkey.DonkeyProvider")
                            .create()
                            .withConfig(config)
                            .build();
    }
}
