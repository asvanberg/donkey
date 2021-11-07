package io.github.asvanberg.donkey.benchmarks;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public final class Person
{
    private final int id;
    private final String name;
    private final List<String> aliases;
    private final OptionalInt age;
    private final Optional<LocalDate> birthday;

    @JsonbCreator
    public Person(
            @JsonbProperty("id") final int id,
            @JsonbProperty("name") final String name,
            @JsonbProperty("aliases") final List<String> aliases,
            @JsonbProperty("age") final OptionalInt age,
            @JsonbProperty("birthday") final Optional<LocalDate> birthday)
    {
        this.id = id;
        this.name = name;
        this.aliases = aliases;
        this.age = age;
        this.birthday = birthday;
    }

    @JsonbProperty("id")
    public int getId()
    {
        return id;
    }

    @JsonbProperty("name")
    public String getName()
    {
        return name;
    }

    @JsonbProperty("aliases")
    public List<String> getAliases()
    {
        return aliases;
    }

    @JsonbProperty("age")
    public OptionalInt getAge()
    {
        return age;
    }

    @JsonbProperty("birthday")
    public Optional<LocalDate> getBirthday()
    {
        return birthday;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        return o instanceof Person person
                && id == person.id
                && name.equals(person.name)
                && aliases.equals(person.aliases)
                && age.equals(person.age)
                && birthday.equals(person.birthday);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, aliases, age, birthday);
    }
}
