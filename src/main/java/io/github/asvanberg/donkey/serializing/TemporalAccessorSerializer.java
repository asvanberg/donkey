package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

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
