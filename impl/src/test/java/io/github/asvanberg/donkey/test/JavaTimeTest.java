package io.github.asvanberg.donkey.test;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JavaTimeTest extends DefaultConfigurationTest {
    @Test
    public void instant_serialize_as_iso_format_by_default() {
        final Instant now = Instant.now();
        final String expected = DateTimeFormatter.ISO_INSTANT.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json)
                .isString()
                .isEqualTo(expected);
    }

    @Test
    public void offset_date_time_serialize_as_iso_format_by_default() {
        final OffsetDateTime now = OffsetDateTime.now();
        final String expected = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json)
                .isString()
                .isEqualTo(expected);
    }

    @Property
    public void instant(@ForAll Instant instant) {
        final String json = jsonb.toJson(instant);
        final Instant deserialized = jsonb.fromJson(json, Instant.class);
        assertThat(deserialized)
                .isEqualTo(instant);
    }

    @Property
    public void offset_date_time(@ForAll OffsetDateTime offsetDateTime) {
        final String json = jsonb.toJson(offsetDateTime);
        final OffsetDateTime deserialized = jsonb.fromJson(json, OffsetDateTime.class);
        assertThat(deserialized)
                .isEqualTo(offsetDateTime);
    }

    @Test
    public void local_date_time_serialize_as_iso_format_by_default() {
        final LocalDateTime now = LocalDateTime.now();
        final String expected = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json)
                .isString()
                .isEqualTo(expected);
    }

    @Property
    public void local_date_time(@ForAll LocalDateTime localDateTime) {
        final String json = jsonb.toJson(localDateTime);
        final LocalDateTime deserialized = jsonb.fromJson(json, LocalDateTime.class);
        assertThat(deserialized)
                .isEqualTo(localDateTime);
    }

    @Test
    public void local_date_serialize_as_iso_format_by_default() {
        final LocalDate now = LocalDate.now();
        final String expected = DateTimeFormatter.ISO_LOCAL_DATE.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json)
                .isString()
                .isEqualTo(expected);
    }

    @Property
    public void local_date(@ForAll LocalDate localDate) {
        final String json = jsonb.toJson(localDate);
        final LocalDate deserialized = jsonb.fromJson(json, LocalDate.class);
        assertThat(deserialized)
                .isEqualTo(localDate);
    }

    @Test
    public void local_time_serialize_as_iso_format_by_default() {
        final LocalTime now = LocalTime.now();
        final String expected = DateTimeFormatter.ISO_LOCAL_TIME.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json)
                .isString()
                .isEqualTo(expected);
    }

    @Property
    public void local_time(@ForAll LocalTime localTime) {
        final String json = jsonb.toJson(localTime);
        final LocalTime deserialized = jsonb.fromJson(json, LocalTime.class);
        assertThat(deserialized)
                .isEqualTo(localTime);
    }
}
