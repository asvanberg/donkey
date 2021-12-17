package io.github.asvanberg.donkey.apt.test;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URISyntaxException;
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

class Compiler
{
    static Boolean compileFile(
            String file,
            Processor annotationProcessor,
            DiagnosticCollector<JavaFileObject> diagnosticListener)
            throws IOException, URISyntaxException
    {
        return compileFile(annotationProcessor, diagnosticListener, List.of(resourceToPath(file)));
    }

    static Boolean compileDirectory(
            String directory,
            Processor annotationProcessor,
            DiagnosticCollector<JavaFileObject> diagnosticListener)
            throws IOException, URISyntaxException
    {
        return compileFile(annotationProcessor, diagnosticListener, getFilesInDirectory(directory));
    }

    private static Boolean compileFile(
            Processor annotationProcessor,
            DiagnosticCollector<JavaFileObject> diagnosticListener,
            List<Path> paths)
            throws IOException
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
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

            Iterable<? extends JavaFileObject> javaFileObjects
                    = fileManager.getJavaFileObjectsFromPaths(paths);

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnosticListener,
                    null,
                    null,
                    javaFileObjects);
            task.setProcessors(Set.of(annotationProcessor));

            return task.call();
        }
    }

    private static List<Path> getFilesInDirectory(String directory)
            throws IOException, URISyntaxException
    {
        return Files.list(resourceToPath(directory))
                    .collect(Collectors.toList());
    }

    private static Path resourceToPath(String file)
            throws URISyntaxException
    {
        URL resource = Compiler.class.getResource(file);
        assert resource != null;
        return Paths.get(resource.toURI());
    }
}
