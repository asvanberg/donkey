package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

class TemporalAccessorSerializer implements JsonbSerializer<TemporalAccessorProperty> {
    private final Locale defaultLocale;

    TemporalAccessorSerializer(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public void serialize(
            final TemporalAccessorProperty obj,
            final JsonGenerator generator,
            final SerializationContext ctx)
    {
        if (Objects.equals(JsonbDateFormat.DEFAULT_FORMAT, obj.pattern())) {
            ctx.serialize(obj.temporalAccessor(), generator);
        }
        else if (Objects.equals(JsonbDateFormat.TIME_IN_MILLIS, obj.pattern())) {
            final long epochSecond = obj.temporalAccessor().getLong(ChronoField.INSTANT_SECONDS);
            final int nanos = obj.temporalAccessor().get(ChronoField.NANO_OF_SECOND);
            final long epochMillis = TimeUnit.SECONDS.toMillis(epochSecond)
                    + TimeUnit.NANOSECONDS.toMillis(nanos);
            generator.write(epochMillis);
        }
        else {
            final Locale locale = getLocale(obj.locale());
            final DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern(obj.pattern(), locale);
            generator.write(dateTimeFormatter.format(obj.temporalAccessor()));
        }
    }

    private Locale getLocale(final String locale) {
        return Objects.equals(JsonbDateFormat.DEFAULT_LOCALE, locale)
                ? this.defaultLocale
                : Locale.forLanguageTag(locale);
    }
}
