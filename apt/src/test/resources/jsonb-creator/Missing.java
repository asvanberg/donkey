package jsonbcreator;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public record Missing(String s, @JsonbProperty("x") int x)
{
    @JsonbCreator
    public Record {}
}
