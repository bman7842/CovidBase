package swd.team11.coviddatabase.server;

import swd.team11.coviddatabase.server.network.DatabaseServer;

/**
 * Main runner for starting server
 */
public class RunServer {

    /**
     * Main method called to start the server
     * @param args  input args
     */
    public static void main(String[] args) {
        DatabaseServer server = new DatabaseServer(23548);
        server.start();
    }


}
