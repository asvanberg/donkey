package deserialization;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Complex(
        @JsonbProperty("x") int x,
        @JsonbProperty("alive") boolean alive,
        @JsonbProperty("friends") List<String> friends,
        @JsonbProperty("table") List<List<Integer>> table,
        @JsonbProperty("favourites") Map<String, List<Color>> favourites,
        @JsonbProperty("parent") Optional<Complex> parent,
        @JsonbProperty("birthday") @JsonbDateFormat("yyyyMMdd")LocalDate birthday,
        @JsonbProperty("wakeUpTime")
        @JsonbDateFormat(value = "hh:mmaa", locale = "sv")
        LocalTime wakeUpTime)
{
    @JsonbCreator
    public Complex {}
}
