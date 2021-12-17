package io.github.asvanberg.donkey.apt.test;

import io.github.asvanberg.donkey.apt.JsonbSerializerGenerator;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonbSerializerGeneratorTest
{
    @Test
    public void compiles_without_warnings()
            throws Exception
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<>();
        try (var fileManager = compiler.getStandardFileManager(
                diagnosticListener,
                Locale.getDefault(),
                StandardCharsets.UTF_8))
        {
            String[] dependencyPaths = System.getProperty("jdk.module.path")
                                             .split(System.getProperty("path.separator"));
            List<Path> dependencies = Arrays.stream(dependencyPaths)
                                            .map(Paths::get)
                                            .collect(Collectors.toList());
            fileManager.setLocationFromPaths(StandardLocation.CLASS_PATH, dependencies);
            Path outputPath = Paths.get(System.getProperty("user.dir"), "target", "apt");

            Files.createDirectories(outputPath);
            fileManager.setLocationFromPaths(StandardLocation.CLASS_OUTPUT, Set.of(outputPath));

            URL javaFileDirectory = JsonbDeserializerGeneratorTest.class.getResource(
                    "/serializer-generation");
            assert javaFileDirectory != null;
            List<Path> javaClassFiles
                    = Files.list(Paths.get(javaFileDirectory.toURI()))
                           .collect(Collectors.toList());
            Iterable<? extends JavaFileObject> javaFileObjects
                    = fileManager.getJavaFileObjectsFromPaths(javaClassFiles);

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnosticListener,
                    null,
                    null,
                    javaFileObjects);
            task.setProcessors(Set.of(new JsonbSerializerGenerator()));

            Boolean compiled = task.call();
            assertThat(diagnosticListener.getDiagnostics())
                    .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                    .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.WARNING)
                    .noneMatch(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.MANDATORY_WARNING);
            assertThat(compiled)
                    .isTrue();
        }
    }
}
