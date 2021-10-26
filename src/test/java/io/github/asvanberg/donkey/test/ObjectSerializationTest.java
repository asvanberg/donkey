package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.NoPropertiesToSerializeException;
import jakarta.json.JsonNumber;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectSerializationTest extends DefaultConfigurationTest {

    @Test
    public void includes_all_annotated_methods()
    {
        final var bob = new Person("Bob", "Builder", 17, false);
        final String jsonString = jsonb.toJson(bob);
        SerializationUtils.assertParsedJson(jsonString, jsonValue ->
                jsonValue
                        .isObject()
                        .hasEntrySatisfying("first_name", property ->
                                assertThat(property)
                                        .isString()
                                        .isEqualTo(bob.firstName()))
                        .hasEntrySatisfying("last_name", property ->
                                assertThat(property)
                                        .isString()
                                        .isEqualTo(bob.lastName()))
                        .hasEntrySatisfying("age", property ->
                                assertThat(property)
                                        .isNumber()
                                        .extracting(JsonNumber::intValue)
                                        .isEqualTo(bob.age()))
                        .hasEntrySatisfying("deceased", property ->
                                assertThat(property)
                                        .isBoolean()
                                        .isEqualTo(bob.deceased())));
    }

    public static record Person(
            @JsonbProperty("first_name") String firstName,
            @JsonbProperty("last_name") String lastName,
            @JsonbProperty("age") int age,
            @JsonbProperty("deceased") boolean deceased)
    {
    }

    @Test
    public void skips_methods_without_annotation()
    {
        final var bob = new NoAnnotation("Bob", 5);
        final String json = jsonb.toJson(bob);
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .doesNotContainKey("name"));
    }

    public static record NoAnnotation(String name, @JsonbProperty("age") int age) {
    }

    @Test
    public void skips_methods_without_annotation_value()
    {
        final var bob = new NoAnnotationValue("Bob", 5);
        final String json = jsonb.toJson(bob);
        assertFalse(json.contains(bob.name()), "Should not contain name");
    }

    public static record NoAnnotationValue(@JsonbProperty String name, @JsonbProperty("age") int age) {
    }

    @Test
    public void errors_on_trying_to_serialize_object_without_any_properties() {
        final Object obj = new Object();
        try {
            jsonb.toJson(obj);
            fail("Should fail when trying to serialize object without any properties defined");
        }
        catch (NoPropertiesToSerializeException ignored) {
        }
    }

    @Test
    public void uses_specific_serializer()
    {
        final String str = "A string that is not a palindrome";
        final var bob = new SpecificSerializer(str);
        final String json = jsonb.toJson(bob);
        SerializationUtils.assertParsedJson(json, jsonValue ->
                jsonValue.isObject()
                         .hasEntrySatisfying("name", property ->
                                 assertThat(property).isString()
                                                     .isEqualTo(reverse(str))));
    }

    public static record SpecificSerializer(
            @JsonbProperty("name") @JsonbTypeSerializer(ReversingSerializer.class) String name)
    {
        public static class ReversingSerializer implements JsonbSerializer<String> {
            @Override
            public void serialize(final String obj, final JsonGenerator generator, final SerializationContext ctx)
            {
                generator.write(reverse(obj));
            }
        }
    }

    private static String reverse(final String str)
    {
        return new StringBuilder(str).reverse().toString();
    }
}
