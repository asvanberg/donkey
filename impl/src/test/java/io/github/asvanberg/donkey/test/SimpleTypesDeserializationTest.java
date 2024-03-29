package io.github.asvanberg.donkey.test;

import io.github.asvanberg.donkey.exceptions.UnexpectedParserPositionException;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimpleTypesDeserializationTest extends DefaultConfigurationTest
{
    @Test
    public void integer()
    {
        final int i = 42;
        final Integer value = jsonb.fromJson(Integer.toString(i), int.class);
        assertThat(value)
                .isEqualTo(i);
    }

    @Test
    public void long_() {
        String json = """
                42
                """;

        final long value = jsonb.fromJson(json, long.class);
        assertThat(value).isEqualTo(42);
    }

    @Test
    public void big_integer_from_number()
    {
        String json = """
                981723981
                """;

        BigInteger bigInteger = jsonb.fromJson(json, BigInteger.class);
        assertThat(bigInteger)
                .isEqualTo(981723981L);
    }

    @Test
    public void big_integer_from_string()
    {
        String json = """
                "56283597234912012417294812141208795102412019823071029123"
                """;

        BigInteger bigInteger = jsonb.fromJson(json, BigInteger.class);
        assertThat(bigInteger)
                .isEqualTo("56283597234912012417294812141208795102412019823071029123");
    }

    @Test
    public void big_integer_from_faulty_json_type()
    {
        String json = """
                [1]
                """;

        assertThatThrownBy(() -> jsonb.fromJson(json, BigInteger.class))
                .isInstanceOf(UnexpectedParserPositionException.class);
    }

    @Property
    public void big_decimal_from_number_scientific(@ForAll BigDecimal wanted)
    {
        String json = wanted.toString();

        BigDecimal actual = jsonb.fromJson(json, BigDecimal.class);
        assertThat(actual)
                .isEqualTo(wanted);
    }


    @Property
    public void big_decimal_from_number_plain(@ForAll BigDecimal wanted)
    {
        String json = wanted.toPlainString();

        BigDecimal actual = jsonb.fromJson(json, BigDecimal.class);
        assertThat(actual)
                .isEqualTo(wanted);
    }


    @Property
    public void big_decimal_from_string_scientific(@ForAll BigDecimal wanted)
    {
        String json = "\"" + wanted.toString() + "\"";

        BigDecimal actual = jsonb.fromJson(json, BigDecimal.class);
        assertThat(actual)
                .isEqualTo(wanted);
    }

    @Property
    public void big_decimal_from_string_plain(@ForAll BigDecimal wanted)
    {
        String json = "\"" + wanted.toPlainString() + "\"";

        BigDecimal actual = jsonb.fromJson(json, BigDecimal.class);
        assertThat(actual)
                .isEqualTo(wanted);
    }

    @Test
    public void big_decimal_fails_for_object_type()
    {
        String json = """
                {}
                """;

        assertThatThrownBy(() -> jsonb.fromJson(json, BigDecimal.class))
                .isInstanceOf(UnexpectedParserPositionException.class);
    }
}
