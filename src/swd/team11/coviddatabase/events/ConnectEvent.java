package swd.team11.coviddatabase.events;

import swd.team11.coviddatabase.user.Client;

/**
 * Interface for creating a client connect event listener
 */
public interface ConnectEvent {

    /**
     * Event called when client successfully connects to server
     * @param client    Client that connected
     */
    public void clientConnected(Client client);

}
