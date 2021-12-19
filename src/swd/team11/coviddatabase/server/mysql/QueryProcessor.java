package swd.team11.coviddatabase.server.mysql;

import javafx.util.Pair;
import swd.team11.coviddatabase.events.MySQLQueryEvent;
import swd.team11.coviddatabase.server.network.DatabaseServer;
import swd.team11.coviddatabase.utils.CircularBuffer;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;
import swd.team11.coviddatabase.utils.RequestType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Thread for processing queries in the CircularBuffer
 */
public class QueryProcessor implements Runnable {

    /**
     * Circular buffer queries are obtained from
     */
    private CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType,String>>> updateBuffer;
    /**
     * Database server reference
     */
    private DatabaseServer dbs;

    /**
     * Construct a query processor object
     * @param updateBuffer  Buffer queries are stored to
     * @param dbs           Reference to database server
     */
    public QueryProcessor(CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType,String>>> updateBuffer, DatabaseServer dbs) {
        this.updateBuffer = updateBuffer;
        this.dbs = dbs;
    }

    /**
     * Called when thread is started, begins checking query buffer for any queries to send/receive from MySQL database
     */
    @Override
    public void run() {
        MySQLConnection mySQLConnection = null;
        try {
            mySQLConnection = dbs.getDatabaseConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Pair<MySQLQueryEvent, Pair<RequestType,String>> queryPair = updateBuffer.blockingGet();

                MySQLQueryEvent listener = queryPair.getKey();
                Pair<RequestType, String> request = queryPair.getValue();

                if (request.getKey().equals(RequestType.UPDATE)) {
                    if (mySQLConnection != null) {
                        mySQLConnection.updateQuery(request.getValue());
                        queryPair.getKey().querySentEvent(new Packet(PacketType.SUCCESS, queryPair.getValue()));
                    } else {
                        queryPair.getKey().querySentEvent(new Packet(PacketType.ERROR, "Unable to process query, no MySQL connection."));
                    }
                } else if (request.getKey().equals(RequestType.EXECUTE)) {
                    if (mySQLConnection != null) {
                        ResultSet rs = mySQLConnection.executeQuery(request.getValue());
                        System.out.println("Calling result received event");
                        queryPair.getKey().resultReceivedEvent(rs);
                    } else {
                        queryPair.getKey().resultReceivedEvent(null);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
