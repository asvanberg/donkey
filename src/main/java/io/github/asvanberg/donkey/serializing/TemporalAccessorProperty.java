package io.github.asvanberg.donkey.serializing;

import java.time.temporal.TemporalAccessor;

public record TemporalAccessorProperty(
        String pattern,
        String locale,
        TemporalAccessor temporalAccessor)
{
}
