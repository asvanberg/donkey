package jsonbcreator;

import jakarta.json.bind.annotation.JsonbProperty;

public class NotRecord
{
    public NotRecord(@JsonbProperty("x") int x, int y) {
    }
}