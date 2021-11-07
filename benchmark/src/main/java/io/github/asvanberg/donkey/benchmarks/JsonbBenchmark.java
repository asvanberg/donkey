package io.github.asvanberg.donkey.benchmarks;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.spi.JsonbProvider;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Fork(1)
@State(Scope.Benchmark)
public class JsonbBenchmark
{
    private static final Person PERSON = new Person(
            42, "Bob", List.of("Bobobity"), OptionalInt.of(35),
            Optional.of(LocalDate.of(2021, Month.OCTOBER, 6)));
    private static final String PERSON_JSON = """
            {"name":"Bob","id":42,"aliases":["Bobobity"],"age":35,"birthday":"2021-10-06"}
            """;
    private static final String STRING = "hello world!";
    private static final String STRING_JSON = """
            "hello world!"
            """;
    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.of(
            LocalDate.of(2021, Month.OCTOBER, 26),
            LocalTime.of(17, 4, 13),
            ZoneOffset.ofHours(2));
    private static final String OFFSET_DATE_TIME_JSON = """
            "2021-10-26T17:04:13+02:00"
            """;
    private static final List<Integer> LIST = List.of(1, 1, 2, 3, 5, 8, 13, 21, 34, 55);
    private static final String LIST_JSON = """
            [ 1, 1, 2, 3, 5, 8, 13, 21, 34, 55]
            """;
    private static final Type LIST_OF_INTEGER_TYPE;

    static {
        @SuppressWarnings("unused")
        abstract class GenericType<T>
        {
        }
        final Type genericSuperclass = new GenericType<List<Integer>>()
        {
        }.getClass().getGenericSuperclass();
        LIST_OF_INTEGER_TYPE = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
    }

    @Param({"io.github.asvanberg.donkey.DonkeyProvider", "org.eclipse.yasson.JsonBindingProvider"})
    private String provider;

    private Jsonb jsonb;

    @Setup
    public void initiateJsonb()
    {
        jsonb = JsonbProvider.provider(provider)
                             .create()
                             .build();
    }

    @Benchmark
    public void serialize_string(final Blackhole blackhole)
    {
        final String json = jsonb.toJson(STRING);
        blackhole.consume(json);
    }

    @Benchmark
    public void deserialize_string(final Blackhole blackhole)
    {
        final String s = jsonb.fromJson(STRING_JSON, String.class);
        blackhole.consume(s);
    }

    @Benchmark
    public void serialize_list_of_integers(final Blackhole blackhole)
    {
        final String json = jsonb.toJson(LIST);
        blackhole.consume(json);
    }

    @Benchmark
    public void deserialize_list_of_integers(final Blackhole blackhole)
    {
        final List<Integer> integers = jsonb.fromJson(LIST_JSON, LIST_OF_INTEGER_TYPE);
        blackhole.consume(integers);
    }

    @Benchmark
    public void serialize_offset_date_time(final Blackhole blackhole)
    {
        final String json = jsonb.toJson(OFFSET_DATE_TIME);
        blackhole.consume(json);
    }

    @Benchmark
    public void deserialize_offset_date_time(final Blackhole blackhole)
    {
        final OffsetDateTime odt = jsonb.fromJson(OFFSET_DATE_TIME_JSON, OffsetDateTime.class);
        blackhole.consume(odt);
    }

    @Benchmark
    public void serialize_object(final Blackhole blackhole)
    {
        final String json = jsonb.toJson(PERSON);
        blackhole.consume(json);
    }

    @Benchmark
    public void deserialize_object(final Blackhole blackhole)
    {
        final Person person = jsonb.fromJson(PERSON_JSON, Person.class);
        blackhole.consume(person);
    }
}
