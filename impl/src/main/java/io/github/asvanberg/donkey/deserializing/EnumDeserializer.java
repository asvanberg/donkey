package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;

class EnumDeserializer<T extends Enum<T>> implements JsonbDeserializer<Enum<T>>
{
    private final Class<T> clazz;

    EnumDeserializer(final Class<T> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public Enum<T> deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return Enum.valueOf(clazz, parser.getString());
    }
}
