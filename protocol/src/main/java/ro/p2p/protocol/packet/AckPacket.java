package ro.p2p.protocol.packet;

import ro.p2p.common.enums.PacketType;

public class AckPacket implements Packet {

    private final PacketType acknowledgedType;
    private final String acknowledgedId;

    public AckPacket(PacketType acknowledgedType, String acknowledgedId) {
        if (acknowledgedType == null) {
            throw new IllegalArgumentException("Acknowledged type must not be null");
        }
        if (acknowledgedId == null || acknowledgedId.isBlank()) {
            throw new IllegalArgumentException("Acknowledged id must not be blank");
        }
        this.acknowledgedType = acknowledgedType;
        this.acknowledgedId = acknowledgedId;
    }

    @Override
    public PacketType getType() {
        return PacketType.ACK;
    }

    public PacketType getAcknowledgedType() {
        return acknowledgedType;
    }

    public String getAcknowledgedId() {
        return acknowledgedId;
    }
}
