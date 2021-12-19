package io.github.asvanberg.donkey.test;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionDeserializationTest extends DefaultConfigurationTest {
    @Test
    public void list_of_integers()
    {
        final List<Integer> integers = List.of(1, 2, 3);
        testCollection(integers, new P<>() {
        });
    }

    @Test
    public void list_of_list_of_integers()
    {
        final List<List<Integer>> integers = List.of(List.of(1, 2, 3), List.of(), List.of(4));
        testCollection(integers, new P<>() {
        });
    }

    @Test
    public void map_of_string_to_int()
    {
        final Map<String, Integer> map = Map.of("age", 30, "height", 180);
        testCollection(map, new P<>() {
        });
    }

    @Test
    public void list_of_map_of_list()
    {
        final List<Map<String, List<Integer>>> convoluted =
                List.of(Map.of("A", List.of(1), "B", List.of(2)));
        testCollection(convoluted, new P<>() {
        });
    }

    @Test
    public void map_of_list_of_map()
    {
        final Map<String, List<Map<String, Integer>>> convoluted =
                Map.of("A", List.of(Map.of(), Map.of("b", 2)), "C", List.of());
        testCollection(convoluted, new P<>() {
        });
    }

    @Test
    public void collection_of_strings()
    {
        final Type type = getType(new P<Collection<String>>() {
        });
        final Collection<String> collection = jsonb.fromJson(
                """
                        ["hello", "world"]
                        """,
                type);

        assertThat(collection)
                .contains("hello", "world")
                .size().isEqualTo(2);
    }

    @Test
    public void set_of_strings()
    {
        Type type = getType(new P<Set<String>>(){});
        String json = """
                ["hello", "world", "hello"]
                """;
        Set<String> strings = jsonb.fromJson(json, type);
        assertThat(strings)
                .hasSize(2)
                .contains("hello")
                .contains("world");
    }

    public enum Animal {
        MONKEY, HUMAN, COW
    }

    @Test
    public void should_use_enum_sets()
    {
        Type type = getType(new P<Set<Animal>>(){});
        String json = """
                ["MONKEY", "COW", "COW"]
                """;
        Set<Animal> animals = jsonb.fromJson(json, type);
        assertThat(animals)
                .isInstanceOf(EnumSet.class)
                .hasSize(2)
                .contains(Animal.COW)
                .contains(Animal.MONKEY);
    }

    @Test
    public void nested_enum_sets()
    {
        Type type = getType(new P<Set<Set<Animal>>>() {});
        String json = """
                [["COW", "COW", "COW"], ["HUMAN"], ["HUMAN", "MONKEY"]]
                """;
        Set<Set<Animal>> setOfAnimals = jsonb.fromJson(json, type);
        assertThat(setOfAnimals)
                .isNotInstanceOf(EnumSet.class)
                .hasSize(3)
                .allMatch(EnumSet.class::isInstance);
    }

    private <T> void testCollection(final T convoluted, final P<T> p)
    {
        final T deserialized = jsonb.fromJson(
                jsonb.toJson(convoluted),
                getType(p));
        assertThat(deserialized)
                .isEqualTo(convoluted);
    }

    @SuppressWarnings("unused")
    private static class P<E> {
    }

    private Type getType(P<?> p)
    {
        return ((ParameterizedType) p.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }
}
