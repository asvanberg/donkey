package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.AdaptingFailedException;
import jakarta.json.JsonNumber;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LONG;

public class JsonbAdapterTest extends DefaultConfigurationTest {
    @Override
    protected JsonbConfig config() {
        return super.config()
                .withAdapters(new InstantAsEpochSecond())
                .withAdapters(new OffsetDateTimeAsInstant())
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

    public static class OffsetDateTimeAsInstant implements JsonbAdapter<OffsetDateTime, Instant>
    {
        @Override
        public Instant adaptToJson(final OffsetDateTime obj)
        {
            return obj.toInstant();
        }

        @Override
        public OffsetDateTime adaptFromJson(final Instant obj)
        {
            return obj.atOffset(ZoneOffset.UTC);
        }
    }

    public static class FailingStringAdapter implements JsonbAdapter<String, Integer>
    {
        @Override
        public Integer adaptToJson(final String obj)
                throws Exception
        {
            throw new IOException();
        }

        @Override
        public String adaptFromJson(final Integer obj)
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
                .isInstanceOf(AdaptingFailedException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void failed_adapter_during_deserialization() {
        final String json = """
                123
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, String.class))
                .isInstanceOf(AdaptingFailedException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void adapt_multiple_steps_during_serialization() {
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(
                LocalDateTime.of(2021, Month.OCTOBER, 6, 17, 4, 13),
                ZoneOffset.UTC);

        final String json = jsonb.toJson(offsetDateTime);
        assertParsedJson(json)
                .isNumber()
                .extracting(JsonNumber::longValueExact, LONG)
                .isEqualTo(1633539853L);
    }

    @Test
    public void adapt_multiple_steps_during_deserialization() {
        final OffsetDateTime offsetDateTime = OffsetDateTime.of(
                LocalDateTime.of(2021, Month.OCTOBER, 6, 17, 4, 13),
                ZoneOffset.UTC);

        final OffsetDateTime deserialized = jsonb.fromJson("1633539853", OffsetDateTime.class);
        assertThat(deserialized)
                .isEqualTo(offsetDateTime);
    }
}
