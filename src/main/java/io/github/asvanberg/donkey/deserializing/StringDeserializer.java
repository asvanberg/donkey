package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

class StringDeserializer extends NullableDeserializer<String> {

    StringDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    protected String getValue(final JsonParser parser, final DeserializationContext ctx) {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return parser.getString();
    }
}
