package io.github.asvanberg.donkey.apt.test;

import io.github.asvanberg.donkey.apt.CheckJsonbPropertyValue;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckJsonbPropertyValueTest
{
    @Test
    public void fails_when_jsonb_property_value_is_missing()
            throws Exception
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-property/Missing.java",
                new CheckJsonbPropertyValue(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isFalse();
    }

    @Test
    public void succeeds_when_jsonb_property_value_is_present()
            throws Exception
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-property/Present.java",
                new CheckJsonbPropertyValue(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isTrue();
    }
}
