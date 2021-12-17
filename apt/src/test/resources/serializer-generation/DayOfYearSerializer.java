package serialization;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.LocalDate;

public class DayOfYearSerializer
        implements JsonbSerializer<LocalDate>
{
    @Override
    public void serialize(LocalDate obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write(obj.getDayOfYear());
    }
}