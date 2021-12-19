package swd.team11.coviddatabase.utils;

/**
 * Enum representing the type of packet sent. Notifies receiver what kinda of
 * data is stored in the packet.
 */
public enum PacketType {
    SEND_USER_ACCOUNT,SEND_VACCINATION_INFO, REQUEST, ERROR, SUCCESS, UPDATE_GUIDELINES;
}
