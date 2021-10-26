package io.github.asvanberg.donkey.deserializing;

import jakarta.json.stream.JsonParser;

interface ParserHistory {
    JsonParser.Event currentEvent();
}
