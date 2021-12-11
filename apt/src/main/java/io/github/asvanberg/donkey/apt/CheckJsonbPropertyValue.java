package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbProperty;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Verifies that all parameters annotated with {@link JsonbProperty}
 * have their {@link JsonbProperty#value()} set
 */
public final class CheckJsonbPropertyValue extends AbstractProcessor
{
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
            Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(JsonbProperty.class);
        for (Element e : elements) {
            if (e.getKind() == ElementKind.PARAMETER) {
                JsonbProperty jsonbProperty = e.getAnnotation(JsonbProperty.class);
                if (jsonbProperty.value().isBlank()) {
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Missing @JsonbProperty value on parameter " + e, e);
                }
            }
        }
        return false;
    }
}
