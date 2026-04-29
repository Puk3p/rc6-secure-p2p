package ro.p2p.protocol.packet;

import ro.p2p.common.enums.PacketType;

public interface Packet {

    PacketType getType();
}
