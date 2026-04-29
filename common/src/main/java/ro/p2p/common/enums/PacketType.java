package ro.p2p.common.enums;

public enum PacketType {
    HELLO(0x01),
    MESSAGE(0x02),
    FILE_META(0x03),
    FILE_CHUNK(0x04),
    ACK(0x05),
    ERROR(0x06);

    private final int code;

    PacketType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PacketType fromCode(int code) {
        for (PacketType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown packet type code: " + code);
    }
}
