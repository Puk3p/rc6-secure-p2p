package ro.p2p.messaging.tracker;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AckTracker {

    private final Set<String> acknowledgedIds = ConcurrentHashMap.newKeySet();

    public void markAcknowledged(String id) {
        acknowledgedIds.add(id);
    }

    public boolean isAcknowledged(String id) {
        return acknowledgedIds.contains(id);
    }
}
