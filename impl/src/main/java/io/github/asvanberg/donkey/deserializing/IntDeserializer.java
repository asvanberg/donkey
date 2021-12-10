package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

enum IntDeserializer implements JsonbDeserializer<Integer> {
    INSTANCE;

    @Override
    public Integer deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_NUMBER, parser);
        return parser.getInt();
    }
}
