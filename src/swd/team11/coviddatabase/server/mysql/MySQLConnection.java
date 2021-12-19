package swd.team11.coviddatabase.server.mysql;

import java.sql.*;
import java.util.*;

public class MySQLConnection {

    private String dbClass;
    /**
     * URL of the MySQL server
     */
    private String url;
    /**
     * Properties for MySQL server, must contain 'user' for username and 'password' for password.
     */
    private Properties properties; //Requires user and password field
    /**
     * reference to Connection object created with attemptConnection
     */
    private Connection conn;

    /**
     * Trys to establish connection with the MySQL server url and properties provided.
     * @param url           URL to MySQL server
     * @param properties    Properties for server
     * @throws Exception    Thrown if fails to connect
     */
    public MySQLConnection(String url, Properties properties) throws SQLException {
        this.url = url;
        this.properties = properties;

        conn = attemptConnection(url, properties);
    }

    /**
     * Attempt to connect to the mySQL server based on the URL and properties provided.
     * Connection is returned.
     * @param url   URL of MySQL server
     * @param info  properties of server.
     * @return      Connection object established with MySQL database
     * @throws Exception    thrown if connection fails
     */
    public Connection attemptConnection(String url, Properties info) throws SQLException {
        return DriverManager.getConnection(url, info);
    }

    /**
     * Sends an execute query message to the MySQL server.
     * @param statement Raw statement to send
     * @return          ResultSet returned from statement
     */
    public ResultSet executeQuery(String statement) {
        System.out.println("Attempting to send query '" +statement+ "' to MySQL!");
        if (isConnected()) {
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeQuery(statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Sends a update query message to the MySQL server.
     * @param statement Raw statement to send
     * @return          number returned from statement
     */
    public int updateQuery(String statement) {
        System.out.println("Attempting to send query '" +statement+ "' to MySQL!");
        if (isConnected()) {
            try {
                Statement stmt = conn.createStatement();
                return stmt.executeUpdate(statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * Updates the connection reference for this object.
     * @param connection    new connection made
     */
    public void setConnection(Connection connection) {
        this.conn = connection;
    }

    /**
     * Closes the active Connection object.
     */
    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the connection stored in object.
     * @return  Connection object
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Returns whether a connection has been stored in the object.
     * @return  true if Connection exists, false if not.
     */
    public boolean isConnected() {
        return (conn != null);
    }

    /**
     * Creates a new table with specified columns and their associated type.
     * @param tableName Name of the table
     * @param columns   Columns specified as key being column name and value being the variable type.
     */
    public void createTable(String tableName, LinkedHashMap<String, String> columns) {
        String statement = "CREATE TABLE " + tableName;

        StringJoiner columnJoiner = new StringJoiner(", ");
        for (String column : columns.keySet()) {
            columnJoiner.add(column + " " + columns.get(column));
        }

        statement += "(" + columnJoiner.toString() + ");";
        updateQuery(statement);
    }

    /**
     * Checks whether a table exists in the MySQL database.
     * @param tableName Name of table
     * @return          False if table doesn't exist, true if table was found.
     * @throws SQLException Thrown if issue accessing database
     */
    public boolean tableExist(String tableName) throws SQLException {
        boolean tExists = false;
        try (ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            while (rs.next()) {
                String tName = rs.getString("TABLE_NAME");
                if (tName != null && tName.equals(tableName)) {
                    tExists = true;
                    break;
                }
            }
        }
        return tExists;
    }

    /**
     * Checks if a value is in given column
     * @param column    Name of column to check in
     * @param value     Value you are finding
     */
    public boolean isInColumn(String tableName, String column, String value) {
        String query = "SELECT * from "+ tableName + " WHERE " + column + " = '" +value +"'";
        ResultSet rs = executeQuery(query);
        try {
            rs.next(); //Not sure if this works?
            return true;
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Adds a row to the table with the specified column values
     * @param tableName     Name of the table in question
     * @param columnValues  Values to add, with key being column and value being column value
     * @deprecated Table generates string to be queued to QueryProcessor
     */
    public void addRow(String tableName, HashMap<String, String> columnValues) {
        StringJoiner columnNames = new StringJoiner(", ");
        StringJoiner columnValueNames = new StringJoiner(", ");
        for (String column : columnValues.keySet()) {
            columnNames.add(column);
            columnValueNames.add(columnValues.get(column));
        }

        String query = "INSERT INTO `"+tableName+"` ("+columnNames.toString()+") VALUES ("+columnValueNames.toString()+");";
        updateQuery(query);
    }

    /**
     * Create a resultset of rows with flag value inside the indicator column.
     * @param tableName         Table to check
     * @param indicatorColumn   Column to check for flag value
     * @param flagValue         Desired value we want to keep in result set.
     * @return
     */
    public ResultSet getFlaggedRows(String tableName, String indicatorColumn, String flagValue) {
        String query = "SELECT * from " +tableName + " WHERE " + indicatorColumn + " = '" +flagValue +"'";
        return executeQuery(query);
    }

}
