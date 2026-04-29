package ro.p2p.messaging.tracker;

import java.util.concurrent.atomic.AtomicLong;

public class SequenceManager {

    private final AtomicLong sequence = new AtomicLong();

    public long next() {
        return sequence.incrementAndGet();
    }
}
