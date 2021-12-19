package swd.team11.coviddatabase.utils;

/**
 * Enum storing type of MySQL request made, either update request (for sending date to table)
 * or execute request for receiving data.
 */
public enum RequestType {
    UPDATE, EXECUTE; //execute for sending data, request for requesting (will return resultset), request will return packet
}
