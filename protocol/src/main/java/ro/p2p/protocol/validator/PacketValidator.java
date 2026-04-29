package ro.p2p.protocol.validator;

import ro.p2p.protocol.packet.Packet;

public class PacketValidator {

    public void validate(Packet packet) {
        if (packet == null) {
            throw new IllegalArgumentException("Packet must not be null");
        }
    }
}
