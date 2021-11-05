package io.github.asvanberg.donkey.test;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static io.github.asvanberg.donkey.test.JsonValueAssert.assertThat;
import static io.github.asvanberg.donkey.test.SerializationUtils.assertParsedJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class URITest extends DefaultConfigurationTest
{
    public record URIContainer(@JsonbProperty("uri") URI uri)
    {
        @JsonbCreator
        public URIContainer
        {
        }
    }

    @Test
    public void serialize_uri()
    {
        final URI uri = URI.create("https://example.com");
        final String json = jsonb.toJson(uri);
        assertParsedJson(json)
                .isString()
                .isEqualTo(uri.toString());
    }

    @Test
    public void serialize_uri_as_property()
    {
        final URI uri = URI.create("https://example.com/property");
        final URIContainer uriContainer = new URIContainer(uri);
        final String json = jsonb.toJson(uriContainer);
        assertParsedJson(json)
                .isObject()
                .hasEntrySatisfying("uri", jsonValue ->
                        assertThat(jsonValue)
                                .isString()
                                .isEqualTo(uri.toString()));
    }

    @Test
    public void deserialize_uri()
    {
        final String json = """
                "https://example.com/deserialize"
                """;
        final URI uri = jsonb.fromJson(json, URI.class);
        assertThat(uri)
                .asString()
                .isEqualTo("https://example.com/deserialize");
    }

    @Test
    public void deserialize_uri_as_property()
    {
        final String json = """
                { "uri": "https://example.com/in_object"
                }
                """;
        final URIContainer uriContainer = jsonb.fromJson(json, URIContainer.class);
        assertThat(uriContainer.uri())
                .asString()
                .isEqualTo("https://example.com/in_object");
    }

    @Test
    public void correctly_wraps_uri_syntax_exception()
    {
        final String json = """
                "☠️://foo:bar"
                """;
        assertThatThrownBy(() -> jsonb.fromJson(json, URI.class))
                .isInstanceOf(JsonbException.class)
                .hasCauseInstanceOf(URISyntaxException.class);
    }
}
