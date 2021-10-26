package io.github.asvanberg.donkey.test;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.BooleanAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.StringAssert;

class JsonValueAssert extends AbstractAssert<JsonValueAssert, JsonValue> {
    public static JsonValueAssert assertThat(final JsonValue jsonValue) {
        return new JsonValueAssert(nullify(jsonValue));
    }

    private static JsonValue nullify(final JsonValue jsonValue) {
        if (jsonValue != null && jsonValue.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        else {
            return jsonValue;
        }
    }

    private JsonValueAssert(final JsonValue actual) {
        super(actual, JsonValueAssert.class);
    }

    public StringAssert isString() {
        if (actual instanceof JsonString jsonString) {
            return Assertions.assertThat(jsonString.getString());
        }
        else {
            throw typeFailure(JsonValue.ValueType.STRING);
        }
    }

    public MapAssert<String, JsonValue> isObject() {
        if (actual instanceof JsonObject jsonObject) {
            return Assertions.assertThat(jsonObject);
        }
        else {
            throw typeFailure(JsonValue.ValueType.OBJECT);
        }
    }

    public BooleanAssert isBoolean() {
        return switch (actual.getValueType()) {
            case TRUE -> Assertions.assertThat(true);
            case FALSE -> Assertions.assertThat(false);
            default -> throw failure("Expected value to be of type <%s> or <%s> but was %s",
                    JsonValue.ValueType.TRUE,
                    JsonValue.ValueType.FALSE,
                    actual.getValueType());
        };
    }

    public ListAssert<JsonValue> isArray() {
        if (actual instanceof JsonArray jsonArray) {
            return Assertions.assertThat(jsonArray);
        }
        else {
            throw typeFailure(JsonValue.ValueType.ARRAY);
        }
    }

    public ObjectAssert<JsonNumber> isNumber() {
        if (actual instanceof JsonNumber jsonNumber) {
            return Assertions.assertThat(jsonNumber);
        }
        else {
            throw typeFailure(JsonValue.ValueType.NUMBER);
        }
    }

    private AssertionError typeFailure(final JsonValue.ValueType expected) {
        return failure("Expected value to be of type <%s> but was <%s>",
                expected,
                actual.getValueType());
    }
}
