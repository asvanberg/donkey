package jsonbcreator;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class Present
{
    @JsonbCreator
    public static Present create(@JsonbProperty("s") String s) {
        return new Present();
    }
}
