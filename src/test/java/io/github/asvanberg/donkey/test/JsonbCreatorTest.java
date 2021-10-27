package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.MissingExplicitJsonbPropertyValueException;
import io.github.asvanberg.donkey.exceptions.MissingJsonbPropertyOnJsonbCreatorParameterException;
import io.github.asvanberg.donkey.exceptions.NoJsonbCreatorException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
}
