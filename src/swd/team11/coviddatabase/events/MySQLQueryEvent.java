package swd.team11.coviddatabase.events;

import swd.team11.coviddatabase.utils.Packet;

import java.sql.ResultSet;

/**
 * Interface representing a MySQLQueryEvent called by QueryProcessor after Query processed to mySQL
 * @see swd.team11.coviddatabase.server.mysql.QueryProcessor
 */
public interface MySQLQueryEvent {

    /**
     * Called when Query has been sent to MySQL database. Only called for update queries
     * @param response  Packet response to send back to client
     */
    public void querySentEvent(Packet response);

    /**
     * Called when query has received resultset from MySQL database. Only called for execute queries
     * @param rs    ResultSet returned for query
     */
    public void resultReceivedEvent(ResultSet rs);
}
