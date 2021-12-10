package io.github.asvanberg.donkey.apt;

import javax.lang.model.element.ExecutableElement;

record Property(ExecutableElement method, String name, boolean nillable)
{
}
