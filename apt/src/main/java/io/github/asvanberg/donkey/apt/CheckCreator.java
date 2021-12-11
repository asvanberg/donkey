package io.github.asvanberg.donkey.apt;

import jakarta.json.bind.annotation.JsonbProperty;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

abstract class CheckCreator extends AbstractProcessor
{
    void check(ExecutableElement e)
    {
        for (VariableElement parameter : e.getParameters()) {
            JsonbProperty jsonbProperty = parameter.getAnnotation(JsonbProperty.class);
            if (jsonbProperty == null) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "Missing @JsonbProperty on parameter " + parameter, parameter);
            }
        }
    }
}
