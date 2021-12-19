package swd.team11.coviddatabase.server.mysql;

import javafx.util.Pair;
import swd.team11.coviddatabase.events.MySQLQueryEvent;
import swd.team11.coviddatabase.utils.CircularBuffer;
import swd.team11.coviddatabase.utils.RequestType;

import java.sql.SQLException;
import java.util.*;

/**
 * Class for representing and constructing queries for a MySQL table
 */
public class Table {

    /**
     * Name of table
     */
    private String name;
    /**
     * Stores column name as key and mysql data type as value
     */
    private LinkedHashMap<String, String> columns;

    /**
     * Construct a table object.
     */
    public Table(String name, LinkedHashMap<String, String> columns) {
        this.name = name;
        this.columns = columns;
    }

    /**
     * Checks if a table exist, if it doesn't it is initialized.
     */
    public void initializeTable(MySQLConnection connection) throws SQLException {
        if (!connection.tableExist(name)) {
            connection.createTable(name, columns);
        }
    }

    /**
     * Updates the value in a given column of the table for the specified username
     * @param username
     * @param column
     */
    public void updateUserColumn(String username, String column) {

    }

    /**
     * Confirms each column key is present in this tables columns keyset, if its present the value
     * will be added to the mysql add row command. This then creates a MySQL connection to send to.
     * @param username
     * @param columnValues
     */
    public void addRow(MySQLQueryEvent listener, CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType,String>>> circularBuffer, String username, HashMap<String, String> columnValues) throws InterruptedException {
        HashMap<String, String> columnsAndUsername = new HashMap<>(columnValues);
        columnsAndUsername.put("username", "'"+username+"'");

        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner columnValueNames = new StringJoiner(", ");
        for (String column : columnsAndUsername.keySet()) {
            columnNames.add(column);
            columnValueNames.add(columnsAndUsername.get(column));
        }

        String query = "INSERT INTO "+name+"("+columnNames.toString()+") VALUES ("+columnValueNames.toString()+");";

        circularBuffer.blockingPut(new Pair<MySQLQueryEvent, Pair<RequestType,String>>(listener, new Pair<>(RequestType.UPDATE, query)));
        //mySQLConnection.addRow(name, columnsAndUsername);
    }

    public void updateColumnInRow(MySQLQueryEvent listener, CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType,String>>> circularBuffer, String username, String column, String newValue) throws InterruptedException {
        //String query = "INSERT INTO " + name + " (username, " +column +") VALUES ('"+username+"', '"+ newValue + "') ON DUPLICATE KEY UPDATE " + column + "='"+newValue+"'";
        String query = "REPLACE INTO " + name + " (username, " +column +") VALUES ('"+username+"', '"+ newValue + "')";

        circularBuffer.blockingPut(new Pair<MySQLQueryEvent, Pair<RequestType, String>>(listener, new Pair<>(RequestType.UPDATE, query)));
    }

    /*
    public ArrayList<String> getColumnStrings(MySQLConnection connection, String username, String column) {
        ResultSet rs = obtainResultSet(connection, username);
        if (rs!=null) {
            try {
                ArrayList<String> values = new ArrayList<String>();
                while(rs.next()) {
                    values.add(rs.getString(column));
                }
                return values;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
*/

    /*
    public ArrayList<Integer> getColumnInts(MySQLConnection connection, String username, String column) {
        ResultSet rs = obtainResultSet(connection, username);
        if (rs!=null) {
            try {
                ArrayList<Integer> values = new ArrayList<Integer>();
                while(rs.next()) {
                    values.add(rs.getInt(column));
                }
                return values;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
*/

    /*
    public ArrayList<Date> getColumnDates(MySQLConnection connection, String username, String column) {
        ResultSet rs = obtainResultSet(connection, username);
        if (rs!=null) {
            try {
                ArrayList<Date> values = new ArrayList<Date>();
                while(rs.next()) {
                    values.add(rs.getDate(column));
                }
                return values;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    */

    /**
     * Submit query to obtain result for specified username
     * @param listener          Listener to notify when query request completed
     * @param circularBuffer    Circular buffer to add query to
     * @param username          Username we are searching for
     * @throws InterruptedException If circular buffer interrupted
     */
    public void obtainResultSet(MySQLQueryEvent listener, CircularBuffer<Pair<MySQLQueryEvent, Pair<RequestType, String>>> circularBuffer, String username) throws InterruptedException {
        String query = "SELECT * from "+ name + " WHERE " + "username" + " = '" +username +"'";
        circularBuffer.blockingPut(new Pair<>(listener, new Pair<>(RequestType.EXECUTE, query)));
    }

    /**
     * Returns the name of the table.
     * @return  string for table name
     */
    public String getName() {
        return name;
    }


}
