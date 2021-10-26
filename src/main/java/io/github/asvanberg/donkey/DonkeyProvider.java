package io.github.asvanberg.donkey;

import io.github.asvanberg.donkey.deserializing.Deserializer;
import io.github.asvanberg.donkey.serializing.Serializers;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.spi.JsonbProvider;
import jakarta.json.spi.JsonProvider;

import java.util.Objects;

/**
 * The entry point to use Donkey as your Jakarta JSON Binding implementation.
 */
public class DonkeyProvider extends JsonbProvider {
    @Override
    public JsonbBuilder create() {
        return new DonkeyBuilder();
    }

    private static class DonkeyBuilder implements JsonbBuilder {
        private JsonbConfig config;
        private JsonProvider provider;

        @Override
        public JsonbBuilder withConfig(final JsonbConfig config) {
            this.config = config;
            return this;
        }

        @Override
        public JsonbBuilder withProvider(final JsonProvider jsonpProvider) {
            this.provider = jsonpProvider;
            return this;
        }

        @Override
        public Jsonb build() {
            final JsonProvider actualProvider =
                    Objects.requireNonNullElseGet(provider, JsonProvider::provider);
            final JsonbConfig actualConfig =
                    Objects.requireNonNullElseGet(config, JsonbConfig::new);
            return new Donkey(actualProvider, new Serializers(actualConfig), new Deserializer(actualConfig));
        }
    }
}
