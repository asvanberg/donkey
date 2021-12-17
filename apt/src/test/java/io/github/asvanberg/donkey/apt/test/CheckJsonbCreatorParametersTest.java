package io.github.asvanberg.donkey.apt.test;

import io.github.asvanberg.donkey.apt.CheckJsonbCreatorParameters;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckJsonbCreatorParametersTest
{
    @Test
    public void jsonb_property_missing_on_creator_parameter()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/Missing.java",
                new CheckJsonbCreatorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isFalse();
    }

    @Test
    public void jsonb_property_set_on_all_creator_parameters()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/Present.java",
                new CheckJsonbCreatorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isTrue();
    }
}
