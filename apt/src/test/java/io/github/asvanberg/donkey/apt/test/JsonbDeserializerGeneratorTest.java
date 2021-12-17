package io.github.asvanberg.donkey.apt.test;

import io.github.asvanberg.donkey.apt.JsonbDeserializerGenerator;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonbDeserializerGeneratorTest
{
    @Test
    public void compiles_without_warnings()
            throws Exception
    {
        DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<>();
        Boolean compiled = Compiler.compileDirectory(
                "/deserializer-generation",
                new JsonbDeserializerGenerator(),
                diagnosticListener);
        assertThat(diagnosticListener.getDiagnostics())
                .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.WARNING)
                .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.MANDATORY_WARNING);
        assertThat(compiled)
                .isTrue();
    }
}
