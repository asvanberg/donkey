package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDate;

enum LocalDateDeserializer implements JsonbDeserializer<LocalDate> {
    INSTANCE;

    @Override
    public LocalDate deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return LocalDate.parse(parser.getString());
    }
}
