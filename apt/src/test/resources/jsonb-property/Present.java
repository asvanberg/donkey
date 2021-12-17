package jsonbproperty;

import jakarta.json.bind.annotation.JsonbProperty;

public record Present(@JsonbProperty("x") int x)
{
}