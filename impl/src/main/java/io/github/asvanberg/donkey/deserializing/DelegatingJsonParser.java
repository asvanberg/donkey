package io.github.asvanberg.donkey.deserializing;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

abstract class DelegatingJsonParser implements JsonParser {
    private final JsonParser delegate;

    DelegatingJsonParser(final JsonParser delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public Event next() {
        return delegate.next();
    }

    @Override
    public String getString() {
        return delegate.getString();
    }

    @Override
    public boolean isIntegralNumber() {
        return delegate.isIntegralNumber();
    }

    @Override
    public int getInt() {
        return delegate.getInt();
    }

    @Override
    public long getLong() {
        return delegate.getLong();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return delegate.getBigDecimal();
    }

    @Override
    public JsonLocation getLocation() {
        return delegate.getLocation();
    }

    @Override
    public JsonObject getObject() {
        return delegate.getObject();
    }

    @Override
    public JsonValue getValue() {
        return delegate.getValue();
    }

    @Override
    public JsonArray getArray() {
        return delegate.getArray();
    }

    @Override
    public Stream<JsonValue> getArrayStream() {
        return delegate.getArrayStream();
    }

    @Override
    public Stream<Map.Entry<String, JsonValue>> getObjectStream() {
        return delegate.getObjectStream();
    }

    @Override
    public Stream<JsonValue> getValueStream() {
        return delegate.getValueStream();
    }

    @Override
    public void skipArray() {
        delegate.skipArray();
    }

    @Override
    public void skipObject() {
        delegate.skipObject();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
