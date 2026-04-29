package ro.p2p.common.util;

import java.time.Instant;

public final class TimeUtils {

    private TimeUtils() {}

    public static long nowMillis() {
        return Instant.now().toEpochMilli();
    }
}
