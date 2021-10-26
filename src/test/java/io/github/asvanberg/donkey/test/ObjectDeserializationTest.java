package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ObjectDeserializationTest extends DefaultConfigurationTest {
    public static record Point(@JsonbProperty("x") int x, @JsonbProperty("y") int y) {
        @JsonbCreator
        public Point {
        }
    }

    @Test
    public void ignores_unknown_properties() {
        String json = """
                { "list": [1,2,3]
                , "object": { "foo": 1.2 }
                , "string": "string"
                , "y": 2
                , "x": 3
                }
                """;

        final Point point = jsonb.fromJson(json, Point.class);
        assertThat(point).extracting(Point::x).isEqualTo(3);
        assertThat(point).extracting(Point::y).isEqualTo(2);
    }

    @Test
    public void non_object_json() {
        String json = """
                [ 1, 2, 3 ]
                """;

        try {
            jsonb.fromJson(json, Point.class);
            fail("Should have errored with unexpected JSON");
        }
        catch (UnexpectedParserPositionException expected) {
        }
    }

    @Test
    public void invalid_attribute_type() {
        String json = """
                { "x": [ 1 ]
                , "y": 2
                }
                """;

        try {
            jsonb.fromJson(json, Point.class);
            fail("Should have errored with unexpected JSON");
        }
        catch (UnexpectedParserPositionException expected) {
        }
    }
}
