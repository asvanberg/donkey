package io.github.asvanberg.donkey.test;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.UseType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordTest extends DefaultConfigurationTest {
    public static record Person(
            @JsonbProperty("id") int id,
            @JsonbProperty("email") String email,
            @JsonbProperty(value = "name", nillable = true) Optional<String> realName,
            @JsonbProperty("friends") List<String> friends)
    {
        @JsonbCreator
        public Person {
        }
    }

    @Property
    public void record_back_and_forth(@ForAll @UseType Person person) {
        final String json = jsonb.toJson(person);
        final Person roundTrippedPerson = jsonb.fromJson(json, Person.class);
        assertThat(roundTrippedPerson)
                .isEqualTo(person);
    }
}
