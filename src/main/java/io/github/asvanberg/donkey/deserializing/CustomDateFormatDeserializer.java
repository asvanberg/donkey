package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;

class CustomDateFormatDeserializer<T> implements JsonbDeserializer<T> {
    private final DateTimeFormatter dateTimeFormatter;
    private final TemporalQuery<T> temporalQuery;

    CustomDateFormatDeserializer(
            final DateTimeFormatter dateTimeFormatter,
            final TemporalQuery<T> temporalQuery)
    {
        this.dateTimeFormatter = dateTimeFormatter;
        this.temporalQuery = temporalQuery;
    }

    @Override
    public T deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return dateTimeFormatter.parse(parser.getString(), temporalQuery);
    }
}
