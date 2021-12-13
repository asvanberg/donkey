package io.github.asvanberg.donkey.test;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

public class AptDeserializerTest extends DefaultConfigurationTest
{
    public static record Container(String s)
    {
    }

    @SuppressWarnings("unused")
    public static class Container$donkey$apt$deserializer
            implements JsonbDeserializer<Container>
    {

        @Override
        public Container deserialize(
                JsonParser parser, DeserializationContext ctx, Type rtType)
        {
            return new Container("apt");
        }
    }

    @Test
    public void uses_apt_deserializer_if_it_exists()
    {
        Container container = jsonb.fromJson("", Container.class);
        assertThat(container)
                .extracting(Container::s)
                .isEqualTo("apt");
    }
}
