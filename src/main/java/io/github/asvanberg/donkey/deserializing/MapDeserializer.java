package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class MapDeserializer implements JsonbDeserializer<Map<String, ?>>
{
    private final Type valueType;

    public MapDeserializer(final Type[] parameters)
    {
        this.valueType = parameters[1];
    }

    @Override
    public Map<String, ?> deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        final Map<String, Object> map = new HashMap<>();
        while (parser.next() != JsonParser.Event.END_OBJECT) {
            final String key = parser.getString();
            parser.next();
            final Object value = ctx.deserialize(valueType, parser);
            map.put(key, value);
        }
        return map;
    }
}
