package io.github.asvanberg.donkey.test;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;
import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;

public class AptSerializerTest extends DefaultConfigurationTest
{
    public static record Point(@JsonbProperty("x") int x, @JsonbProperty("y") int y)
    {
    }

    @SuppressWarnings("unused")
    public static class Point$donkey$apt$serializer implements JsonbSerializer<Point>
    {
        @Override
        public void serialize(
                Point obj, JsonGenerator generator, SerializationContext ctx)
        {
            generator.writeStartObject();
            ctx.serialize("x", obj.x(), generator);
            ctx.serialize("y", obj.y(), generator);
            ctx.serialize("apt", true, generator);
            generator.writeEnd();
        }
    }

    @Test
    public void prefers_apt_generated_serializer_if_exists()
    {
        String json = jsonb.toJson(new Point(3, 4));
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("apt", jsonValue ->
                        assertThat(jsonValue)
                                .isBoolean()
                                .isTrue());
    }
}
