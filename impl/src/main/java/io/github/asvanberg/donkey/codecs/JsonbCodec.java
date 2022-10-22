package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;

public interface JsonbCodec<T> extends JsonbSerializer<T>, JsonbDeserializer<T> {
}
