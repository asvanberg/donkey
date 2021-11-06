package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.MissingExplicitJsonbPropertyValueException;
import io.github.asvanberg.donkey.exceptions.MissingJsonbPropertyOnJsonbCreatorParameterException;
import io.github.asvanberg.donkey.exceptions.MissingPropertyInJsonException;
import io.github.asvanberg.donkey.exceptions.NoJsonbCreatorException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonbCreatorTest extends DefaultConfigurationTest {
    public record Point(int x, int y) {
        @JsonbCreator
        public static Point at(
                @JsonbProperty("x") final int x,
                @JsonbProperty("y") final int y)
        {
            return new Point(x, y);
        }
    }

    @Test
    public void static_factory_method() {
        String json = """
                { "x": 1
                , "y": 2
                }
                """;
        final Point point = jsonb.fromJson(json, Point.class);

        assertThat(point).extracting(Point::x).isEqualTo(1);
        assertThat(point).extracting(Point::y).isEqualTo(2);
    }

    public static record MissingJsonbCreator(int x) {
    }

    @Test
    public void jsonb_creator_annotation_required_on_custom_classes() {
        String json = """
                { "x": 1
                }
                """;
        try {
            jsonb.fromJson(json, MissingJsonbCreator.class);
            fail("Should throw exception when no JsonbCreator exists");
        } catch (NoJsonbCreatorException expected) {
        }
    }

    public static record MissingJsonbProperty(int x) {
        @JsonbCreator
        public MissingJsonbProperty {}
    }

    @Test
    public void parameters_require_jsonb_property_annotation() {
        String json = """
                { "x": 1
                }
                """;
        try {
            jsonb.fromJson(json, MissingJsonbProperty.class);
            fail("Should throw exception requiring JsonbProperty annotation");
        } catch (MissingJsonbPropertyOnJsonbCreatorParameterException expected) {
        }
    }

    public static record MissingJsonbPropertyValue(@JsonbProperty int x) {
        @JsonbCreator
        public MissingJsonbPropertyValue {}
    }

    @Test
    public void parameters_require_explicit_jsonb_property_value() {
        String json = """
                { "x": 1
                }
                """;
        try {
            jsonb.fromJson(json, MissingJsonbPropertyValue.class);
            fail("Should throw exception requiring explicit JsonbProperty annotation value()");
        } catch (MissingExplicitJsonbPropertyValueException expected) {
        }
    }

    public record TestRecord(
            @JsonbProperty("enabled")
            boolean enabled,
            @JsonbProperty("name")
            String name,
            @JsonbProperty("aliases")
            List<String> aliases,
            @JsonbProperty("age")
            OptionalInt age,
            @JsonbProperty("height")
            OptionalLong height,
            @JsonbProperty("weight")
            OptionalDouble weight,
            @JsonbProperty("birthday")
            Optional<LocalDate> birthday)
    {
        @JsonbCreator
        public TestRecord
        {
        }
    }

    @Test
    public void missing_required_primitive_parameter()
    {
        final String json = """
                { "name": "Donkey"
                , "aliases": ["The Donk"]
                , "age": 1
                , "height": 180
                , "weight": 71.2
                , "birthday": "2021-10-06"
                }
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, TestRecord.class))
                .isInstanceOf(MissingPropertyInJsonException.class)
                .hasMessageContaining("enabled");
    }

    @Test
    public void missing_required_reference_parameter()
    {
        final String json = """
                { "enabled": false
                , "aliases": ["The Donk"]
                , "age": 1
                , "height": 180
                , "weight": 71.2
                , "birthday": "2021-10-06"
                }
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, TestRecord.class))
                .isInstanceOf(MissingPropertyInJsonException.class)
                .hasMessageContaining("name");
    }

    @Test
    public void missing_required_collection_parameter()
    {
        final String json = """
                { "enabled": false
                , "name": "Donkey"
                , "age": 1
                , "height": 180
                , "weight": 71.2
                , "birthday": "2021-10-06"
                }
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, TestRecord.class))
                .isInstanceOf(MissingPropertyInJsonException.class)
                .hasMessageContaining("aliases");
    }

    @Test
    public void missing_optional_parameter()
    {
        final String json = """
                { "enabled": true
                , "name": "Donkey"
                , "aliases": ["The Donk"]
                , "age": 1
                , "height": 180
                , "weight": 71.2
                }
                """;
        final TestRecord testRecord = jsonb.fromJson(json, TestRecord.class);
        assertThat(testRecord.birthday())
                .isEmpty();
    }

    @Test
    public void missing_optional_int_parameter()
    {
        final String json = """
                { "enabled": true
                , "name": "Donkey"
                , "aliases": ["The Donk"]
                , "height": 180
                , "weight": 71.2
                , "birthday": "2021-10-06"
                }
                """;
        final TestRecord testRecord = jsonb.fromJson(json, TestRecord.class);
        assertThat(testRecord.age())
                .isEmpty();
    }

    @Test
    public void missing_optional_long_parameter()
    {
        final String json = """
                { "enabled": true
                , "name": "Donkey"
                , "aliases": ["The Donk"]
                , "age": 1
                , "weight": 71.2
                , "birthday": "2021-10-06"
                }
                """;
        final TestRecord testRecord = jsonb.fromJson(json, TestRecord.class);
        assertThat(testRecord.height())
                .isEmpty();
    }

    @Test
    public void missing_optional_double_parameter()
    {
        final String json = """
                { "enabled": true
                , "name": "Donkey"
                , "aliases": ["The Donk"]
                , "age": 1
                , "height": 180
                , "birthday": "2021-10-06"
                }
                """;
        final TestRecord testRecord = jsonb.fromJson(json, TestRecord.class);
        assertThat(testRecord.weight())
                .isEmpty();
    }
}
