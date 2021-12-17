package jsonbcreator;

import jakarta.json.bind.annotation.JsonbProperty;

public record NotCanonicalMissing(String s, int x, int y)
{
    public NotCanonicalMissing(@JsonbProperty("x") int x, int y) {
        this("default", x, y);
    }

    public NotCanonicalMissing(int x, int y, @JsonbProperty("name") String name) {
        this(name, x, y);
    }

    public static NotCanonicalMissing create(@JsonbProperty("x") int x, int y) {
        return new NotCanonicalMissing("default", x, y);
    }
}
