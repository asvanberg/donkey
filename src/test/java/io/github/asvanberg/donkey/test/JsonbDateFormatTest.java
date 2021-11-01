package io.github.asvanberg.donkey.test;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;
import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonbDateFormatTest extends DefaultConfigurationTest {

    private static final LocalDateTime FIRST_COMMIT = LocalDateTime.of(
            LocalDate.of(2021, Month.OCTOBER, 6),
            LocalTime.of(17, 4, 13));

    @Override
    public JsonbConfig config() {
        return super.config()
                    .withLocale(Locale.ENGLISH);
    }

    public record CustomLocalDateFormat(
            @JsonbProperty("birthday") @JsonbDateFormat("MMddyy") LocalDate birthday)
    {
        @JsonbCreator
        public CustomLocalDateFormat {
        }
    }

    @Test
    public void serialize_local_date_with_custom_format() {
        final CustomLocalDateFormat customLocalDateFormat
                = new CustomLocalDateFormat(FIRST_COMMIT.toLocalDate());
        final String json = jsonb.toJson(customLocalDateFormat);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("birthday", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("100621"));
    }

    @Test
    public void deserialize_local_date_with_custom_format() {
        final String json = """
                {"birthday":"100621"}
                """;
        final CustomLocalDateFormat customLocalDateFormat =
                jsonb.fromJson(json, CustomLocalDateFormat.class);
        assertThat(customLocalDateFormat.birthday())
                .isEqualTo(FIRST_COMMIT.toLocalDate());
    }

    public record CustomLocalTimeFormat(
            @JsonbProperty("wake_up_time") @JsonbDateFormat("HHmm") LocalTime wakeUpTime)
    {
        @JsonbCreator
        public CustomLocalTimeFormat {
        }
    }

    @Test
    public void serialize_local_time_with_custom_format() {
        final LocalTime wakeUpTime = LocalTime.of(9, 30);
        final CustomLocalTimeFormat customLocalTimeFormat = new CustomLocalTimeFormat(wakeUpTime);
        final String json = jsonb.toJson(customLocalTimeFormat);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("wake_up_time", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("0930"));
    }

    @Test
    public void deserialize_local_time_with_custom_format() {
        final String json = """
                {"wake_up_time":"0930"}
                """;
        final CustomLocalTimeFormat customLocalTimeFormat =
                jsonb.fromJson(json, CustomLocalTimeFormat.class);
        assertThat(customLocalTimeFormat.wakeUpTime())
                .isEqualTo(LocalTime.of(9, 30));
    }

    public record CustomLocalDateTimeFormat(
            @JsonbProperty("datetime") @JsonbDateFormat("yyyyMMdd HHmmss") LocalDateTime dateTime)
    {
        @JsonbCreator
        public CustomLocalDateTimeFormat {
        }
    }

    @Test
    public void serialize_local_date_time_with_custom_format() {
        var customLocalDateTimeFormat = new CustomLocalDateTimeFormat(FIRST_COMMIT);
        final String json = jsonb.toJson(customLocalDateTimeFormat);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("datetime", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("20211006 170413"));
    }

    @Test
    public void deserialize_local_date_time_with_custom_format() {
        final String json = """
                {"datetime":"20211006 170413"}
                """;
        var customLocalDateTimeFormat = jsonb.fromJson(json, CustomLocalDateTimeFormat.class);
        assertThat(customLocalDateTimeFormat.dateTime())
                .isEqualTo(FIRST_COMMIT);
    }

    public record CustomOffsetDateTimeFormat(
            @JsonbProperty("time")
            @JsonbDateFormat("'On' MMMM d uuuu 'at' h:mma O")
            OffsetDateTime time)
    {
        @JsonbCreator
        public CustomOffsetDateTimeFormat {
        }
    }

    @Test
    public void serialize_offset_date_time_with_custom_format() {
        final ZoneOffset offset = ZoneOffset.ofHours(2);
        final OffsetDateTime time = OffsetDateTime.of(FIRST_COMMIT, offset);
        var customInstantFormat = new CustomOffsetDateTimeFormat(time);
        final String json = jsonb.toJson(customInstantFormat);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("time", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("On October 6 2021 at 5:04PM GMT+2"));
    }

    @Test
    public void deserialize_offset_date_time_with_custom_format() {
        final ZoneOffset offset = ZoneOffset.ofHours(2);
        final OffsetDateTime time = OffsetDateTime.of(FIRST_COMMIT.withSecond(0), offset);

        final String json = """
                {"time":"On October 6 2021 at 5:04PM GMT+2"}
                """;
        final CustomOffsetDateTimeFormat customOffsetDateTimeFormat =
                jsonb.fromJson(json, CustomOffsetDateTimeFormat.class);

        assertThat(customOffsetDateTimeFormat.time())
                .isEqualTo(time);
    }

    public record DefaultFormatCustomLocale(
            @JsonbProperty("date")
            @JsonbDateFormat(locale = "sv-SE")
            LocalDate date)
    {
    }

    @Test
    public void serialize_using_default_format_and_different_locale() {
        final String json = jsonb.toJson(new DefaultFormatCustomLocale(FIRST_COMMIT.toLocalDate()));
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("date", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("2021-10-06"));
    }

    public record CustomFormatCustomLocale(
            @JsonbProperty("month")
            @JsonbDateFormat(value = "MMMM", locale = "sv-SE")
            LocalDate date)
    {
    }

    @Test
    public void serialize_using_custom_format_and_different_locale() {
        final String json = jsonb.toJson(new CustomFormatCustomLocale(FIRST_COMMIT.toLocalDate()));
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("month", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("oktober"));
    }

    public record DefaultFormat(@JsonbProperty("date_time") LocalDateTime dateTime) {
    }

    @Test
    public void serialize_without_jsonb_date_format() {
        final String json = jsonb.toJson(new DefaultFormat(FIRST_COMMIT));
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("date_time", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("2021-10-06T17:04:13"));
    }

    public record CustomLocaleDeserialization(
            @JsonbProperty("birthday")
            @JsonbDateFormat(value = "d MMMM uuuu", locale = "sv-SE")
            LocalDate birthday)
    {
        @JsonbCreator
        public CustomLocaleDeserialization {
        }
    }

    @Test
    public void deserialize_with_custom_locale() {
        final String json = """
                {"birthday":"6 oktober 2021"}
                """;
        final CustomLocaleDeserialization customLocaleDeserialization
                = jsonb.fromJson(json, CustomLocaleDeserialization.class);
        assertThat(customLocaleDeserialization.birthday())
                .isEqualTo(FIRST_COMMIT.toLocalDate());
    }

    public record DifferentLocales(
            @JsonbProperty("date1")
            @JsonbDateFormat(value = "MMMM d uuuu", locale = "en")
            LocalDate date1,
            @JsonbProperty("date2")
            @JsonbDateFormat(value = "MMMM d uuuu", locale = "sv-SE")
            LocalDate date2)
    {
        @JsonbCreator
        public DifferentLocales {
        }
    }

    @Test
    public void serialize_using_different_locales() {
        var differentLocales = new DifferentLocales(
                FIRST_COMMIT.toLocalDate(),
                FIRST_COMMIT.toLocalDate());
        final String json = jsonb.toJson(differentLocales);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("date1", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("October 6 2021"))
                .hasEntrySatisfying("date2", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo("oktober 6 2021"));
    }

    @Test
    public void deserialize_using_different_locales() {
        final String json = """
                { "date1": "October 6 2021"
                , "date2": "oktober 6 2021"
                }
                """;
        final DifferentLocales differentLocales = jsonb.fromJson(json, DifferentLocales.class);
        assertThat(differentLocales.date1())
                .isEqualTo(differentLocales.date2())
                .isEqualTo(FIRST_COMMIT.toLocalDate());
    }
}
