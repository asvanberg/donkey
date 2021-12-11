package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbCreator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.Set;

/**
 * Verifies that methods/constructors annotated with {@link JsonbCreator}
 * have all their parameters annotated with
 * {@link jakarta.json.bind.annotation.JsonbProperty JsonbProperty}.
 */
public final class CheckJsonbCreatorParameters extends CheckCreator
{
    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return Set.of(JsonbCreator.class.getName());
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (roundEnv.processingOver()) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(JsonbCreator.class);
        check(ElementFilter.constructorsIn(elements));
        check(ElementFilter.methodsIn(elements));
        return false;
    }

    private void check(Set<ExecutableElement> creators)
    {
        for (ExecutableElement e : creators) {
            check(e);
        }
    }
}
