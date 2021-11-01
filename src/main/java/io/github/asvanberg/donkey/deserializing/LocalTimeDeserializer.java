package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalTime;

enum LocalTimeDeserializer implements JsonbDeserializer<LocalTime> {
    INSTANCE;

    @Override
    public LocalTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return LocalTime.parse(parser.getString());
    }
}
