package swd.team11.coviddatabase.utils;

import java.io.Serializable;

/**
 * Serializable object designed to be sent between client and server.
 * Used for sending data over network
 */
public class Packet implements Serializable {

    /**
     * Type of data packet sent
     */
    private PacketType type;
    /**
     * Data stored in packet
     */
    private Object data;

    /**
     * Constructor for packet
     * @param type  type of packet
     * @param data  data stored in packet
     */
    public Packet(PacketType type, Object data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Returns the type of the packet
     * @return  packet type
     */
    public PacketType getType() {
        return type;
    }

    /**
     * returns the data in the packet
     * @return  some object (typically UserAccount or String)
     */
    public Object getData() {
        return data;
    }
}
