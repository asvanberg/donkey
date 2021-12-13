package deserialization;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public class Simple
{
    private final boolean flag;

    private Simple(boolean flag) {
        this.flag = flag;
    }

    @JsonbCreator
    public static Simple create(@JsonbProperty("flag") boolean flag) {
        return new Simple(flag);
    }
}