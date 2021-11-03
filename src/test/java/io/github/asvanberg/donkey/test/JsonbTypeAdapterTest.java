package io.github.asvanberg.donkey.test;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import org.junit.jupiter.api.Test;

import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonbTypeAdapterTest extends DefaultConfigurationTest
{
    @JsonbTypeAdapter(StatusAdapter.class)
    enum Status { CREATED, EXECUTING, GRADED }

    public static class StatusAdapter implements JsonbAdapter<Status, String>
    {
        @Override
        public String adaptToJson(final Status obj)
        {
            return obj.name().toLowerCase();
        }

        @Override
        public Status adaptFromJson(final String obj)
        {
            return Status.valueOf(obj.toUpperCase());
        }
    }

    @Test
    public void serialize_with_type_level_annotation() {
        final Status status = Status.CREATED;
        final String json = jsonb.toJson(status);
        assertParsedJson(json)
                .isString()
                .isEqualTo(status.name().toLowerCase());
    }

    @Test
    public void deserialize_with_type_level_annotation() {
        final String json = """
                "executing"
                """;
        final Status status = jsonb.fromJson(json, Status.class);
        assertThat(status)
                .isEqualTo(Status.EXECUTING);
    }
}
