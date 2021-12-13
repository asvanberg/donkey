package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonbDeserializerGenerator extends AbstractProcessor
{
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(JsonbCreator.class.getName());
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(JsonbCreator.class);
        ElementFilter.constructorsIn(elements).forEach(this::generateDeserializer);
        ElementFilter.methodsIn(elements).forEach(this::generateDeserializer);
        return false;
    }

    private void generateDeserializer(ExecutableElement executableElement) {
        Element enclosingElement = executableElement.getEnclosingElement();
        ElementKind enclosingKind = enclosingElement.getKind();
        assert enclosingKind == ElementKind.CLASS || enclosingKind == ElementKind.RECORD;
        List<? extends VariableElement> parameters = executableElement.getParameters();
        int arg = 0;
        StringBuilder initializers = new StringBuilder();
        StringBuilder matchers = new StringBuilder();
        StringBuilder statics = new StringBuilder();
        for (VariableElement parameter : parameters) {
            TypeMirror type = parameter.asType();
            initializers.append(propertyInitializer(arg, type));
            matchers.append(propertyCase(arg, parameter));
            statics.append(appendTypeReference(arg, parameter));
            arg++;
        }
        String argsFound = IntStream.range(0, arg)
                                  .mapToObj(i -> "arg" + i + "_found")
                                  .collect(Collectors.joining(" && "));
        String args = IntStream.range(0, arg)
                                  .mapToObj(i -> "arg" + i)
                                  .collect(Collectors.joining(", "));
        String callCreator = executableElement.getKind() == ElementKind.CONSTRUCTOR
                ? "new %s".formatted(enclosingElement)
                : "%s.%s".formatted(enclosingElement, executableElement.getSimpleName());
        String create = """
                if (%s) {
                    return %s(%s);
                }
                else {
                    throw new JsonbException("Properties missing");
                }
                """.formatted(argsFound, callCreator, args);
        Elements elements = processingEnv.getElementUtils();
        String package_ = elements.getPackageOf(enclosingElement).toString();
        String className = enclosingElement.getSimpleName() + "$donkey$apt$deserializer";
        String source = deserializerSource(
                package_,
                className,
                enclosingElement.asType().toString(),
                statics,
                methodBody(initializers, matchers, create));
        Filer filer = processingEnv.getFiler();
        try {
            JavaFileObject sourceFile
                    = filer.createSourceFile(package_ + "." + className, enclosingElement);
            try (var os = new OutputStreamWriter(sourceFile.openOutputStream(), StandardCharsets.UTF_8)) {
                os.write(source);
            }
        }
        catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.WARNING,
                    "Failed to generate deserializer: " + e.getMessage(), executableElement);
        }
    }

    private CharSequence propertyInitializer(
            int i,
            TypeMirror type)
    {
        return """
                boolean arg%d_found = %s;
                %s arg%1$d = %s;
                """
                .formatted(i, isOptional(type), type, getDefault(type));
    }

    private String getDefault(TypeMirror type) {
        return switch (type.getKind()) {
            case BOOLEAN -> "false";
            case BYTE, LONG, CHAR, INT, SHORT, DOUBLE, FLOAT -> "0";
            default -> {
                if (type instanceof DeclaredType declaredType && isOptional(type)) {
                    yield declaredType.asElement() + ".empty()";
                }
                else {
                    yield "null";
                }
            }
        };
    }

    private boolean isOptional(TypeMirror type) {
        return type.toString().startsWith("java.util.Optional");
    }

    private String propertyCase(int arg, VariableElement parameter) {
        String propertyName = parameter.getAnnotation(JsonbProperty.class).value();
        JsonbDateFormat jsonbDateFormat = parameter.getAnnotation(JsonbDateFormat.class);
        if (jsonbDateFormat != null
                && !jsonbDateFormat.value().equals(JsonbDateFormat.DEFAULT_FORMAT))
        {
            return """
                    case "%s" -> {
                        arg%d_found = true;
                        parser.next();
                        arg%2$d = ARG_%2$d_FORMAT.parse(parser.getString(), %s::from);
                    }
                    """.formatted(propertyName, arg, parameter.asType());
        }
        return """
                case "%s" -> {
                    arg%d_found = true;
                    arg%2$d = ctx.deserialize(ARG_%2$d_TYPE, parser);
                }
                """.formatted(propertyName, arg);
    }

    private String appendTypeReference(int arg, VariableElement parameter) {
        JsonbDateFormat jsonbDateFormat = parameter.getAnnotation(JsonbDateFormat.class);
        if (jsonbDateFormat != null
                && !jsonbDateFormat.value().equals(JsonbDateFormat.DEFAULT_FORMAT))
        {
            if (jsonbDateFormat.locale().equals(JsonbDateFormat.DEFAULT_LOCALE)) {
                return """
                        private static final java.time.format.DateTimeFormatter ARG_%d_FORMAT
                             = java.time.format.DateTimeFormatter.ofPattern("%s");
                        """.formatted(arg, jsonbDateFormat.value());
            }
            else {
                return """
                        private static final java.time.format.DateTimeFormatter ARG_%d_FORMAT
                             = java.time.format.DateTimeFormatter.ofPattern("%s", java.util.Locale.forLanguageTag("%s"));
                        """.formatted(arg, jsonbDateFormat.value(), jsonbDateFormat.locale());
            }
        }
        TypeMirror type = parameter.asType();
        if (type.getKind() == TypeKind.DECLARED) {
            return """
                    private static final Type ARG_%d_TYPE;
                    static {
                        abstract class GenericType<T>{}
                        final Type genericSuperclass = new GenericType<%s>(){}.getClass().getGenericSuperclass();
                        ARG_%1$d_TYPE = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
                    }
                    """
                    .formatted(arg, type);
        }
        else {
            return "private static final Type ARG_%d_TYPE = %s.class;"
                    .formatted(arg, type);
        }
    }

    private String methodBody(CharSequence initializers, CharSequence matchers, String create) {
        return """
                JsonParser.Event event = parser.next();
                assert event == JsonParser.Event.START_OBJECT;
                %s
                while ((event = parser.next()) != JsonParser.Event.END_OBJECT) {
                    assert event == JsonParser.Event.KEY_NAME;
                    switch (parser.getString()) {
                        %s
                        default -> {
                            switch (parser.next()) {
                                case START_ARRAY -> parser.skipArray();
                                case START_OBJECT -> parser.skipObject();
                            }
                        }
                    }
                }
                %s
                """.formatted(initializers, matchers, create);
    }

    private String deserializerSource(
            String package_,
            String className,
            String type,
            CharSequence statics,
            String methodBody)
    {
        return """
                package %s;
                                
                import jakarta.json.bind.JsonbException;
                import jakarta.json.bind.serializer.DeserializationContext;
                import jakarta.json.bind.serializer.JsonbDeserializer;
                import jakarta.json.stream.JsonParser;
                import java.lang.reflect.Type;
                import java.lang.reflect.ParameterizedType;
                                
                @javax.annotation.processing.Generated("%s")
                public class %s implements JsonbDeserializer<%s> {
                    %s
                    
                    @Override
                    public %4$s deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
                        %s
                    }
                }
                """.formatted(
                package_,
                JsonbDeserializerGenerator.class.getName(),
                className,
                type,
                statics,
                methodBody);
    }
}
