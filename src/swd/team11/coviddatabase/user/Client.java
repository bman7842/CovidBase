package swd.team11.coviddatabase.user;

import swd.team11.coviddatabase.events.ConnectEvent;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Object for representing a local Client connection with a DataBaseServers.
 * Handles server to application interactions.
 */
public class Client extends Thread {

    /**
     * Address of server to connect to
     */
    private InetAddress address;
    /**
     * Port of server to connect to
     */
    private int port;
    /**
     * Object input stream created from socket to server
     */
    private ObjectInputStream ois;
    /**
     * object output stream created from socket to server
     */
    private ObjectOutputStream oos;
    /**
     * Socket connecting client to server
     */
    private Socket s;
    /**
     * Value representing whether server is connected or not
     */
    private boolean connected;
    /**
     * Event handlers called when client successfully connects to server
     */
    private Set<ConnectEvent> connectListeners;

    /**
     * Object for handling a local users connection to the database server.
     * @param address   server address
     * @param port      server port
     */
    public Client(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        ois = null;
        oos = null;
        s = null;
        connected = false;
        connectListeners = new HashSet<>();
    }

    /**
     * Called when client thread started, attempts to connect to server and if it succeeds,
     * it will announce the connect event to all listeners.
     */
    @Override
    public void run() {
        while(!connected) {
            try {
                connect();
                announceConnectEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method for attempting connection based on credentials stored
     * in client object.
     * @throws IOException  If unable to connect
     */
    public void connect() throws IOException {
        if (connected) {
            System.out.println("A connection has already been established!");
            return;
        }

        s = new Socket(address, port);
        oos = new ObjectOutputStream(s.getOutputStream());
        ois = new ObjectInputStream(s.getInputStream());

        connected = true;
    }

    /**
     * Method for disconnecting client from server. Should be called when client closes application
     * @throws IOException  Unsuccessful attempt at reaching server
     */
    public void disconnect() throws IOException {
        if (connected) {
            s.close();
            oos.close();
            ois.close();
            connected = false;
        } else {
            System.out.println("Cannot close a connection if one has not been established!");
        }
    }

    /**
     * Add a ConnectEvent listener that will be called when this client successfully
     * connects to the server.
     * @param e event object listener
     * @see ConnectEvent
     */
    public void addConnectListener(ConnectEvent e) {
        connectListeners.add(e);
    }

    /**
     * Announces to all listeners that client has connected, passes the Client object that
     * has connected to the listener event.
     * @see ConnectEvent
     */
    private void announceConnectEvent() {
        for (ConnectEvent e : connectListeners) {
            e.clientConnected(this);
        }
    }

    /**
     * Sends packet to the server
     * @param packet        Packet to send
     * @throws IOException  If unable to get connection with server
     */
    public void sendPacket(Packet packet) throws IOException {
        oos.writeObject(packet);
        oos.flush();
    }

    /**
     * Await a packet being sent from server. Called when you are expecting data to be returned
     * @return                          The packet received from the server
     * @throws IOException              Unable to connect to server
     * @throws ClassNotFoundException   If the object returned by server is not a Packet
     */
    public Packet awaitPacket() throws IOException, ClassNotFoundException {
        return (Packet) ois.readObject();
    }

    /**
     * Request data from server, designed specifically to simplify sending request packet types to the server,
     * where a response is always expected thus the method automatically awaits response after sending request.
     * @param request                   Request message to send to server
     * @return                          Packet returned from server
     * @throws IOException              Unable to establish connection with server
     * @throws ClassNotFoundException   Object returned by server is not Packet
     */
    public Packet requestData(String request) throws IOException, ClassNotFoundException {
        oos.writeObject(new Packet(PacketType.REQUEST, request));
        oos.flush();
        return (Packet) ois.readObject();
    }

}
