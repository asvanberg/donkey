package io.github.asvanberg.donkey.test;

import jakarta.json.JsonNumber;
import jakarta.json.JsonValue;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimitiveSerializationTest extends DefaultConfigurationTest {

    @Test
    public void string() {
        final String json = jsonb.toJson("hello");
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isString().isEqualTo("hello"));
    }

    @Test
    public void integer() {
        final String json = jsonb.toJson(42);
        assertEquals("42", json);
    }

    @Test
    public void big_integer() {
        final String json = jsonb.toJson(BigInteger.TEN);
        assertEquals("10", json);
    }

    @Test
    public void big_decimal() {
        final String json = jsonb.toJson(BigDecimal.valueOf(7.6d));
        assertEquals("7.6", json);
    }

    @Test
    public void long_() {
        final String json = jsonb.toJson(Long.MAX_VALUE);
        assertEquals(Long.toString(Long.MAX_VALUE), json);
    }

    @Test
    public void boolean_() {
        final String json = jsonb.toJson(true);
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isBoolean().isEqualTo(true));
    }

    @Test
    public void double_() {
        final double v = 3.14d;
        final String json = jsonb.toJson(v);
        SerializationUtils.assertParsedJson(json, jsonValue->
                jsonValue
                        .isNumber()
                        .extracting(JsonNumber::doubleValue)
                        .isEqualTo(v));
    }

    @Test
    public void json_value() {
        final JsonValue value = JsonValue.TRUE;
        final String json = jsonb.toJson(value);
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isEqualTo(value));
    }
}
