package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeSerializer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Will generate {@link jakarta.json.bind.serializer.JsonbSerializer JsonbSerializer}
 * instances which require no reflection at runtime to increase performance.
 */
public final class JsonbSerializerGenerator extends AbstractProcessor
{
    private final Set<TypeElement> seen = new HashSet<>();

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return Set.of(JsonbProperty.class.getName());
    }

    @Override
    public boolean process(
            final Set<? extends TypeElement> annotations,
            final RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            return false;
        }
        try {
            var methods = ElementFilter.methodsIn(
                    roundEnv.getElementsAnnotatedWith(JsonbProperty.class));
            for (ExecutableElement method : methods) {
                final Element e = method.getEnclosingElement();
                switch (e.getKind()) {
                    case CLASS, RECORD -> {
                        final TypeElement typeElement = (TypeElement) e;
                        if (seen.add(typeElement)) {
                            generateSerializer(analyze(typeElement));
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            var stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    e.getMessage() + "\n" + stackTrace);
        }
        return false;
    }

    private ClassMetadata analyze(final TypeElement typeElement)
    {
        final List<ExecutableElement> methods = ElementFilter.methodsIn(
                typeElement.getEnclosedElements());
        final List<Property> properties = new ArrayList<>();
        for (ExecutableElement method : methods) {
            if (method.getModifiers().contains(Modifier.PUBLIC)) {
                final JsonbProperty annotation = method.getAnnotation(JsonbProperty.class);
                if (annotation != null && !annotation.value().isBlank()) {
                    properties.add(new Property(method, annotation.value(), annotation.nillable()));
                }
            }
        }
        return new ClassMetadata(typeElement, properties);
    }

    private void generateSerializer(ClassMetadata classMetadata)
            throws IOException
    {
        final Filer filer = processingEnv.getFiler();
        final String serializerClassName
                = classMetadata.typeElement().getSimpleName() + "$donkey$apt$serializer";
        final PackageElement packageElement
                = processingEnv.getElementUtils().getPackageOf(classMetadata.typeElement());
        final String serializerFQN
                = packageElement.getQualifiedName() + "." + serializerClassName;
        final JavaFileObject sourceFile
                = filer.createSourceFile(serializerFQN, classMetadata.typeElement());
        try (var os = new OutputStreamWriter(sourceFile.openOutputStream(),
                                             StandardCharsets.UTF_8))
        {
            os.write(serializerSource(classMetadata, packageElement, serializerClassName));
        }
    }

    private String serializerSource(
            final ClassMetadata classMetadata,
            final PackageElement packageElement,
            final String serializerClassName)
    {
        final String serializedType = classMetadata.fqn().toString();
        final StringBuilder methodBody = new StringBuilder();
        final StringBuilder statics = new StringBuilder();
        for (Property property : classMetadata.properties()) {
            serializeProperty(methodBody, statics, property);
        }
        return """
                package %5$s;
                                
                import jakarta.json.bind.serializer.JsonbSerializer;
                import jakarta.json.bind.serializer.SerializationContext;
                import jakarta.json.stream.JsonGenerator;
                                
                @javax.annotation.processing.Generated("%s")
                public final class %s implements JsonbSerializer<%s> {
                    %6$s
                    
                    @Override
                    public void serialize(final %3$s obj, final JsonGenerator generator, final SerializationContext ctx) {
                        generator.writeStartObject();
                        %s
                        generator.writeEnd();
                    }
                    
                    private static boolean isNotNull(final java.util.Optional<?> optional) {
                        return optional.isPresent();
                    }
                    
                    private static boolean isNotNull(final java.util.OptionalInt optional) {
                        return optional.isPresent();
                    }
                    
                    private static boolean isNotNull(final java.util.OptionalLong optional) {
                        return optional.isPresent();
                    }
                    
                    private static boolean isNotNull(final java.util.OptionalDouble optional) {
                        return optional.isPresent();
                    }
                    
                    private static boolean isNotNull(final Object o) {
                        return o != null;
                    }
                }
                """.formatted(
                JsonbSerializerGenerator.class.getName(),
                serializerClassName,
                serializedType,
                methodBody,
                packageElement.getQualifiedName(),
                statics
        );
    }

    private void serializeProperty(
            final StringBuilder methodBody,
            final StringBuilder statics,
            final Property property)
    {
        final ExecutableElement method = property.method();
        final TypeMirror returnType = method.getReturnType();
        final Name methodName = method.getSimpleName();
        if (returnType.getKind().isPrimitive()) {
            appendSerializeProperty(methodBody, methodName, property.name());
        }
        else {
            if (!property.nillable()) {
                methodBody.append("if (isNotNull(obj.").append(methodName).append("())) {\n");
            }

            final TypeMirror temporalAccessorType
                    = processingEnv.getElementUtils()
                                   .getTypeElement("java.time.temporal.TemporalAccessor")
                                   .asType();
            final boolean isTemporalAccessor
                    = processingEnv.getTypeUtils()
                                   .isAssignable(returnType, temporalAccessorType);

            final JsonbDateFormat jsonbDateFormat
                    = method.getAnnotation(JsonbDateFormat.class);
            final JsonbTypeSerializer jsonbTypeSerializer
                    = method.getAnnotation(JsonbTypeSerializer.class);
            if (jsonbTypeSerializer != null) {
                methodBody.append("generator.writeKey(\"")
                          .append(property.name())
                          .append("\");\n");
                methodBody.append("$serializer$")
                          .append(methodName)
                          .append(".serialize(obj.")
                          .append(methodName)
                          .append("(), generator, ctx);\n");

                final String serializerClass = getSerializerFQN(jsonbTypeSerializer);
                statics.append("private static final JsonbSerializer<")
                       .append(returnType)
                       .append("> $serializer$")
                       .append(methodName)
                       .append(" = new ")
                       .append(serializerClass)
                       .append("();\n");
            }
            else if (isTemporalAccessor && jsonbDateFormat != null) {
                if (JsonbDateFormat.TIME_IN_MILLIS.equals(jsonbDateFormat.value())) {
                    appendSerializeProperty(methodBody,
                                            methodName + "().toEpochMilli",
                                            property.name());
                }
                else {
                    statics.append(buildDateTimeFormatter(jsonbDateFormat, methodName));
                    methodBody.append("ctx.serialize(\"")
                              .append(property.name())
                              .append("\", ARG_")
                              .append(methodName)
                              .append("_FORMAT.format(obj.")
                              .append(methodName)
                              .append("()), generator);\n");
                }
            }
            else {
                appendSerializeProperty(methodBody, methodName, property.name());
            }

            if (!property.nillable()) {
                methodBody.append("}\n");
            }
        }
    }

    private String buildDateTimeFormatter(JsonbDateFormat jsonbDateFormat, CharSequence arg) {
        if (jsonbDateFormat.locale().equals(JsonbDateFormat.DEFAULT_LOCALE)) {
            return """
                    private static final java.time.format.DateTimeFormatter ARG_%s_FORMAT
                         = java.time.format.DateTimeFormatter.ofPattern("%s");
                    """.formatted(arg, jsonbDateFormat.value());
        }
        else {
            return """
                    private static final java.time.format.DateTimeFormatter ARG_%s_FORMAT
                         = java.time.format.DateTimeFormatter.ofPattern("%s", java.util.Locale.forLanguageTag("%s"));
                    """.formatted(arg, jsonbDateFormat.value(), jsonbDateFormat.locale());
        }
    }

    private String getSerializerFQN(JsonbTypeSerializer jsonbTypeSerializer)
    {
        try {
            return jsonbTypeSerializer.value().getName();
        } catch (MirroredTypeException e) {
            return e.getTypeMirror().toString();
        }
    }

    private void appendSerializeProperty(
            final StringBuilder stringBuilder,
            final CharSequence methodName,
            final String propertyName)
    {
        stringBuilder.append("ctx.serialize(\"")
                     .append(propertyName)
                     .append("\", obj.")
                     .append(methodName)
                     .append("(), generator);\n");
    }
}
