package io.github.asvanberg.donkey.deserializing;

import jakarta.json.stream.JsonParser;

class PeekableJsonParser extends DelegatingJsonParser {

    private Event next;

    PeekableJsonParser(final JsonParser delegate) {
        super(delegate);
    }

    public PeekableJsonParser(final JsonParser parser, final Event event) {
        this(parser);
        this.next = event;
    }

    public Event peek() {
        next = next();
        return next;
    }

    @Override
    public boolean hasNext() {
        return next != null || super.hasNext();
    }

    @Override
    public Event next() {
        if (next == null) {
            return super.next();
        }
        else {
            Event returnValue = next;
            next = null;
            return returnValue;
        }
    }
}
