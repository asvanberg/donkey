package io.github.asvanberg.donkey.deserializing;

import io.github.asvanberg.donkey.exceptions.InternalProcessingException;
import io.github.asvanberg.donkey.exceptions.MissingExplicitJsonbPropertyValueException;
import io.github.asvanberg.donkey.exceptions.MissingJsonbPropertyOnJsonbCreatorParameterException;
import io.github.asvanberg.donkey.exceptions.NoJsonbCreatorException;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract class Creator<T> {
    static record Parameter(Type type, int index) {
    }

    static <T> Creator<T> forClass(Class<T> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.isAnnotationPresent(JsonbCreator.class)) {
                return new ConstructorCreator<>(getParameters(constructor), constructor);
            }
        }
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(JsonbCreator.class) && Modifier.isStatic(method.getModifiers())) {
                return new MethodCreator<>(getParameters(method), method);
            }
        }
        throw new NoJsonbCreatorException(clazz);
    }

    private static Map<String, Parameter> getParameters(final Executable executable) {
        final java.lang.reflect.Parameter[] parameters = executable.getParameters();
        final Map<String, Parameter> creationParameters = new HashMap<>(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            final var parameter = parameters[i];
            final JsonbProperty jsonbProperty = parameter.getAnnotation(JsonbProperty.class);
            if (jsonbProperty == null) {
                throw new MissingJsonbPropertyOnJsonbCreatorParameterException(parameter, executable);
            }
            final String propertyName = jsonbProperty.value();
            if (propertyName.isBlank()) {
                throw new MissingExplicitJsonbPropertyValueException(parameter, executable);
            }
            creationParameters.put(propertyName, new Parameter(getType(parameter), i));
        }
        return creationParameters;
    }

    private static Type getType(final java.lang.reflect.Parameter parameter) {
        final JsonbDateFormat jsonbDateFormat = parameter.getAnnotation(JsonbDateFormat.class);
        if (jsonbDateFormat != null) {
            return new CustomDateFormatType(
                    jsonbDateFormat.value(),
                    jsonbDateFormat.locale(),
                    parameter.getType());
        }
        else {
            return parameter.getParameterizedType();
        }
    }

    private final Map<String, Parameter> parameters;

    private Creator(final Map<String, Parameter> parameters) {
        this.parameters = parameters;
    }

    int parameterCount() {
        return parameters.size();
    }

    Optional<Parameter> getParameterByName(String parameterName) {
        return Optional.ofNullable(parameters.get(parameterName));
    }

    abstract T create(Object[] creationParameters);

    private static class ConstructorCreator<T> extends Creator<T> {
        private final Constructor<?> constructor;

        public ConstructorCreator(final Map<String, Parameter> creationParameters, final Constructor<?> constructor) {
            super(creationParameters);
            this.constructor = constructor;
        }

        @SuppressWarnings("unchecked")
        @Override
        T create(final Object[] creationParameters) {
            try {
                return (T) constructor.newInstance(creationParameters);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new InternalProcessingException(e);
            }
        }
    }

    private static class MethodCreator<T> extends Creator<T> {
        private final Method method;

        public MethodCreator(final Map<String, Parameter> parameters, final Method method) {
            super(parameters);
            this.method = method;
        }

        @SuppressWarnings("unchecked")
        @Override
        T create(final Object[] creationParameters) {
            try {
                return (T) method.invoke(null, creationParameters);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new InternalProcessingException(e);
            }
        }
    }

}
