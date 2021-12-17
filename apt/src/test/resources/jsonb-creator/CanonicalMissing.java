package jsonbcreator;

import jakarta.json.bind.annotation.JsonbProperty;

public record CanonicalMissing(String s, @JsonbProperty("x") int x)
{
}
