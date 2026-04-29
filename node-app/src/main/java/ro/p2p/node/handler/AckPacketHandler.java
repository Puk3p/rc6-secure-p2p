package ro.p2p.node.handler;

import ro.p2p.messaging.tracker.AckTracker;
import ro.p2p.protocol.packet.AckPacket;

public class AckPacketHandler {

    private final AckTracker ackTracker;

    public AckPacketHandler(AckTracker ackTracker) {
        this.ackTracker = ackTracker;
    }

    public void handle(AckPacket packet) {
        ackTracker.markAcknowledged(packet.getAcknowledgedId());
    }
}
