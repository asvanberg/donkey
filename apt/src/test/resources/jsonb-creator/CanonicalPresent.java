package jsonbcreator;

import jakarta.json.bind.annotation.JsonbProperty;

public record CanonicalPresent(@JsonbProperty("s") String s, @JsonbProperty("x") int x)
{
}
