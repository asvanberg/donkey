/**
 * Adds compile time checks and code generation to make
 * working with Donkey faster and more enjoyable.
 */
module io.github.asvanberg.donkey.apt {
    requires transitive java.compiler;
    requires transitive jakarta.json.bind;
    provides javax.annotation.processing.Processor with
            io.github.asvanberg.donkey.apt.CheckJsonbPropertyValue,
            io.github.asvanberg.donkey.apt.CheckJsonbCreatorParameters,
            io.github.asvanberg.donkey.apt.CheckRecordCanonicalConstructorParameters,
            io.github.asvanberg.donkey.apt.JsonbSerializerGenerator;

    exports io.github.asvanberg.donkey.apt;
}
