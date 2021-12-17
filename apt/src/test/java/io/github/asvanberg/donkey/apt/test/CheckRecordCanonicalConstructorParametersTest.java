package io.github.asvanberg.donkey.apt.test;

import io.github.asvanberg.donkey.apt.CheckRecordCanonicalConstructorParameters;
import org.junit.jupiter.api.Test;

import javax.tools.DiagnosticCollector;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckRecordCanonicalConstructorParametersTest
{
    @Test
    public void jsonb_property_missing_on_creator_parameter()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/CanonicalMissing.java",
                new CheckRecordCanonicalConstructorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isFalse();
    }

    @Test
    public void jsonb_property_set_on_all_creator_parameters()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/CanonicalPresent.java",
                new CheckRecordCanonicalConstructorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isTrue();
    }

    @Test
    public void jsonb_property_missing_on_not_canonical_constructor()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/NotCanonicalMissing.java",
                new CheckRecordCanonicalConstructorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isTrue();
    }


    @Test
    public void jsonb_property_missing_on_not_record_constructor()
            throws IOException, URISyntaxException
    {
        Boolean compiled = Compiler.compileFile(
                "/jsonb-creator/NotRecord.java",
                new CheckRecordCanonicalConstructorParameters(),
                new DiagnosticCollector<>());
        assertThat(compiled)
                .isTrue();
    }
}
