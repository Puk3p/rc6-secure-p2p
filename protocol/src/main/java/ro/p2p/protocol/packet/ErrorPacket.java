package ro.p2p.protocol.packet;

import ro.p2p.common.enums.PacketType;

public class ErrorPacket implements Packet {

    private final String errorCode;
    private final String message;

    public ErrorPacket(String errorCode, String message) {
        this.errorCode = errorCode == null ? "UNKNOWN" : errorCode;
        this.message = message == null ? "" : message;
    }

    @Override
    public PacketType getType() {
        return PacketType.ERROR;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
