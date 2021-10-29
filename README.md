# Donkey
Opinionated implementation of the [Jakarta JSON binding API](https://github.com/eclipse-ee4j/jsonb-api).
It is *not* spec compliant and requires explicit configuration with annotations
rather than relying on naming conventions or the Java Beans specification.

## Motivation
Why explicit configuration? Because I want to maximize the freedom of being able to freely refactor your code as
required without fear of breakage. If the application relies on naming conventions there is a much higher chance of
something breaking when refactoring. It also clearly communicates intent when a method is annotated with
`@JsonbProperty("name")`
that it will be included in the JSON output rather than just seeing a method called `getName()`.
There is also the possibility to designate `@JsonbProperty` as "methods annotated with this are used" which can improve
the feedback your IDE can provide instead of potentially falsely marking it as unused.

## Requirements
This library uses reflection so your classes used for (de-)serialization must therefore be at least opened in their module definition.

## Serializing
When serializing objects only public methods annotated with [`@JsonbProperty`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbProperty.html)
and whose [`value`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbProperty.html#value())
is explicitly set will be included in the JSON.

## Deserializing
When deserializing objects they must have a constructor or static method annotated with [`@JsonbCreator`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbCreator.html)
and all parameters must be annotated with `@JsonbProperty` and have an explicit `value` set.

## Roadmap
Checklist for what is required for the 1.0 release.
* [ ] (De-)serializing the following types
  * [x] Primitives (and their wrappers)
  * [x] `String`
  * [x] `Collection<E>` and `List<E>`
  * [x] `Optional` (and the primitive specializations)
  * [ ] Java Time API (`java.time`)
    * [x] `Instant`
    * [x] `OffsetDateTime`
    * [ ] `LocalDate`
    * [ ] `LocalTime`
    * [x] `LocalDateTime`
  * [x] `Map<String, E>`
  * [x] Arbitrary objects with a `@JsonbCreator` method
* [ ] Support [`@JsonbDateFormat`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbDateFormat.html)
  * [ ] Serialization
  * [ ] Deserialization
* [ ] Support [`@JsonbTypeAdapter`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbTypeAdapter.html)
  * [ ] On [`@JsonbCreator`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbCreator.html) parameters
  * [ ] When serializing methods annotated with [`@JsonbProperty`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbProperty.html)
* [ ] Support [`@JsonbTypeDeserializer`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbTypeDeserializer.html) on parameters to `@JsonbCreator`
* [ ] Support [`@JsonbTypeSerializer`](https://javadoc.io/static/jakarta.json.bind/jakarta.json.bind-api/2.0.0/jakarta/json/bind/annotation/JsonbTypeSerializer.html) on methods annotated with `@JsonbProperty`

### Future work
* [ ] Configure `Instant` as ISO format (string), epoch seconds, or epoch milliseconds
  * [ ] Serialization
  * [ ] Deserialization
