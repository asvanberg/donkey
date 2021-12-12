package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbProperty;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

/**
 * Checks records canonical constructor parameters,
 * if any of them are annotated with {@link JsonbProperty}
 * then the others must be as well.
 */
public final class CheckRecordCanonicalConstructorParameters extends CheckCreator
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
                Element maybeConstructor = e.getEnclosingElement();
                if (maybeConstructor.getKind() == ElementKind.CONSTRUCTOR) {
                    Element maybeRecord = maybeConstructor.getEnclosingElement();
                    if (maybeRecord.getKind() == ElementKind.RECORD) {
                        TypeElement record = (TypeElement) maybeRecord;
                        ExecutableElement constructor = (ExecutableElement) maybeConstructor;
                        if (isCanonical(constructor, record)) {
                            check(constructor);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isCanonical(ExecutableElement constructor, TypeElement record)
    {
        List<? extends RecordComponentElement> recordComponents = record.getRecordComponents();
        List<? extends VariableElement> parameters = constructor.getParameters();
        if (recordComponents.size() != parameters.size()) {
            return false;
        }
        Types types = processingEnv.getTypeUtils();
        for (int i = 0; i < recordComponents.size(); i++) {
            RecordComponentElement rc = recordComponents.get(i);
            VariableElement pc = parameters.get(i);
            if (!types.isSameType(rc.asType(), pc.asType())) {
                return false;
            }
        }
        return true;
    }
}
