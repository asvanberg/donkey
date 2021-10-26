package io.github.asvanberg.donkey.test;

import jakarta.json.JsonNumber;
import jakarta.json.bind.annotation.JsonbProperty;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;

public class OptionalSerializationTest extends DefaultConfigurationTest {
    public record IntContainer(@JsonbProperty("age") OptionalInt age)
    {
    }

    @Test
    public void non_nillable_empty_optional_int()
    {
        final String json = jsonb.toJson(new IntContainer(OptionalInt.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .isEmpty());
    }

    @Test
    public void non_nillable_present_optional_int()
    {
        final int value = 3;
        final String json = jsonb.toJson(new IntContainer(OptionalInt.of(value)));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("age", property ->
                                 assertThat(property)
                                         .isNumber()
                                         .extracting(JsonNumber::intValue)
                                         .isEqualTo(value)));
    }

    public record NillableIntContainer(@JsonbProperty(value = "age", nillable = true) OptionalInt age)
    {
    }

    @Test
    public void nillable_empty_optional_int()
    {
        final String json = jsonb.toJson(new NillableIntContainer(OptionalInt.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("age", property ->
                                 assertThat(property)
                                         .isNull()));
    }

    public record LongContainer(@JsonbProperty("length") OptionalLong length)
    {
    }

    @Test
    public void non_nillable_empty_optional_long()
    {
        final String json = jsonb.toJson(new LongContainer(OptionalLong.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .isEmpty());
    }

    @Test
    public void non_nillable_present_optional_long()
    {
        final long value = 3;
        final String json = jsonb.toJson(new LongContainer(OptionalLong.of(value)));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("length", property ->
                                 assertThat(property)
                                         .isNumber()
                                         .extracting(JsonNumber::longValue)
                                         .isEqualTo(value)));
    }

    public record NillableLongContainer(@JsonbProperty(value = "length", nillable = true) OptionalLong length)
    {
    }

    @Test
    public void nillable_empty_optional_long()
    {
        final String json = jsonb.toJson(new NillableLongContainer(OptionalLong.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("length", property ->
                                 assertThat(property)
                                         .isNull()));
    }

    public record DoubleContainer(@JsonbProperty("temperature") OptionalDouble temperature)
    {
    }

    @Test
    public void non_nillable_empty_optional_double()
    {
        final String json = jsonb.toJson(new DoubleContainer(OptionalDouble.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .isEmpty());
    }

    @Test
    public void non_nillable_present_optional_double()
    {
        final double value = 3.14d;
        final String json = jsonb.toJson(new DoubleContainer(OptionalDouble.of(value)));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("temperature", property ->
                                 assertThat(property)
                                         .isNumber()
                                         .extracting(JsonNumber::doubleValue)
                                         .isEqualTo(value)));
    }

    public record NillableDoubleContainer(
            @JsonbProperty(value = "temperature", nillable = true) OptionalDouble temperature)
    {
    }

    @Test
    public void nillable_empty_optional_double()
    {
        final String json = jsonb.toJson(new NillableDoubleContainer(OptionalDouble.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("temperature", property ->
                                 assertThat(property)
                                         .isNull()));
    }

    public record OptionalContainer(@JsonbProperty(value = "name") Optional<String> name)
    {
    }

    @Test
    public void non_nillable_empty_optional()
    {
        final String json = jsonb.toJson(new OptionalContainer(Optional.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .isEmpty());
    }

    @Test
    public void non_nillable_present_optional()
    {
        final String value = "Bob";
        final String json = jsonb.toJson(new OptionalContainer(Optional.of(value)));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("name", property ->
                                 assertThat(property)
                                         .isString()
                                         .isEqualTo(value)));
    }

    public record NillableOptionalContainer(@JsonbProperty(value = "name", nillable = true) Optional<String> name)
    {
    }

    @Test
    public void nillable_empty_optional()
    {
        final String json = jsonb.toJson(new NillableOptionalContainer(Optional.empty()));
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("name", property ->
                                 assertThat(property)
                                         .isNull()));
    }
}
