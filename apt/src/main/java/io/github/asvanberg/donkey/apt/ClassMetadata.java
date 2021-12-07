package io.github.asvanberg.donkey.apt;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.Collection;

record ClassMetadata(TypeElement typeElement, Collection<Property> properties)
{
    Name fqn() {
        return typeElement.getQualifiedName();
    }
}
