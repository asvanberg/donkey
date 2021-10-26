package io.github.asvanberg.donkey.test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.spi.JsonbProvider;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class DefaultConfigurationTest
{
    protected Jsonb jsonb;

    @BeforeEach
    @BeforeProperty
    public final void init_donkey()
    {
        jsonb = JsonbProvider.provider("io.github.asvanberg.donkey.DonkeyProvider")
                             .create()
                             .build();
    }

    @AfterEach
    @AfterProperty
    public final void shutdown_donkey()
            throws Exception
    {
        jsonb.close();
    }
}
