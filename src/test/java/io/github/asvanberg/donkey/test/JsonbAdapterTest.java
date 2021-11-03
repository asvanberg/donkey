package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonbAdapterTest extends DefaultConfigurationTest {
    @Override
    protected JsonbConfig config() {
        return super.config()
                .withAdapters(new InstantAsEpochSecond())
                .withAdapters(new FailingStringAdapter());
    }

    public static class InstantAsEpochSecond implements JsonbAdapter<Instant, Long>
    {
        @Override
        public Long adaptToJson(final Instant obj)
        {
            return obj.getEpochSecond();
        }

        @Override
        public Instant adaptFromJson(final Long obj)
        {
            return Instant.ofEpochSecond(obj);
        }
    }

    public static class FailingStringAdapter implements JsonbAdapter<String, Object>
    {
        @Override
        public Object adaptToJson(final String obj)
                throws Exception
        {
            throw new IOException();
        }

        @Override
        public String adaptFromJson(final Object obj)
                throws Exception
        {
            throw new IOException();
        }
    }

    @Test
    public void serialize_with_global_adapter() {
        final long epochSecond = 273239492L;
        final Instant instant = Instant.ofEpochSecond(epochSecond);
        final String json = jsonb.toJson(instant);
        assertParsedJson(json)
                .isNumber()
                .satisfies(number -> assertThat(number.isIntegral()).isTrue())
                .satisfies(number -> assertThat(number.longValue()).isEqualTo(epochSecond));
    }

    @Test
    public void deserialize_with_global_adapter() {
        final long epochSecond = 698329672L;
        final String json = """
                %d
                """.formatted(epochSecond);
        final Instant instant = jsonb.fromJson(json, Instant.class);
        assertThat(instant.getEpochSecond())
                .isEqualTo(epochSecond);
    }

    @Test
    public void failed_adapter_during_serialization() {
        final String str = "hello";
        assertThatThrownBy(() -> jsonb.toJson(str))
                .isInstanceOf(AdaptingFailedException.class);
    }

    @Test
    public void failed_adapter_during_deserialization() {
        final String json = """
                "hello"
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, String.class))
                .isInstanceOf(AdaptingFailedException.class);
    }
}
