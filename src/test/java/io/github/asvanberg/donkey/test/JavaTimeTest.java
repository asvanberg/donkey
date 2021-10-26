package io.github.asvanberg.donkey.test;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;

public class JavaTimeTest extends DefaultConfigurationTest {
    @Test
    public void instant_serialize_as_iso_format_by_default() {
        final Instant now = Instant.now();
        final String expected = DateTimeFormatter.ISO_INSTANT.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json, jsonValueAssert ->
                jsonValueAssert.isString()
                               .isEqualTo(expected));
    }

    @Test
    public void offset_date_time_serialize_as_iso_format_by_default() {
        final OffsetDateTime now = OffsetDateTime.now();
        final String expected = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(now);
        final String json = jsonb.toJson(now);
        assertParsedJson(json, jsonValueAssert ->
                jsonValueAssert.isString()
                               .isEqualTo(expected));
    }
}
