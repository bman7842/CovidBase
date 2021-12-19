package swd.team11.coviddatabase.server.network;

import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class for handling server connection with client. Send and receive data and establish a socket connection
 */
public class ClientHandler extends Thread {

    /**
     * Store whether client connection to server is active
     */
    private boolean active;
    /**
     * Socket to server
     */
    private Socket s;
    /**
     * Input stream from client
     */
    private ObjectInputStream ois;
    /**
     * Output stream to client
     */
    private ObjectOutputStream oos;
    /**
     * Database server reference
     */
    private DatabaseServer dbs;

    /**
     * ClientHandler constructor, called everytime a new client connects to server. A thread per client
     * @param dbs       Reference to databaseserver that created client
     * @param socket    Reference to socket client is connected on.
     */
    public ClientHandler(DatabaseServer dbs, Socket socket) {
        active = false;
        this.dbs = dbs;
        this.s = socket;
    }

    /**
     * Get the input and output streams on the socket.
     * @throws IOException  If socket fails
     */
    private void getStreams() throws IOException {
        ois = new ObjectInputStream(s.getInputStream());
        oos = new ObjectOutputStream(s.getOutputStream());
    }

    /**
     * Main method called when thread starts, gets the input output streams and continually listens for data sent from client
     * usually in the form of request packets.
     */
    @Override
    public void run() {
        active = true;

        try {
            getStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(active) {
            try {
                Packet inData = (Packet)ois.readObject();

                if (inData.getType().equals(PacketType.REQUEST)) {
                    RequestHandler requestHandler = new RequestHandler(this, inData, dbs);
                    requestHandler.processRequest();
                } else if (inData.getType().equals(PacketType.SEND_VACCINATION_INFO) | inData.getType().equals(PacketType.UPDATE_GUIDELINES)) {
                    SendDataHandler sendDataHandler = new SendDataHandler(inData, this, dbs);
                    sendDataHandler.processPacket();
                }

            } catch (Exception e) {
                e.printStackTrace();
                disconnect();
            }
        }

        try {
            ois.close();
            oos.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send packet to client
     * @param packet    packet to send
     */
    public void sendPacket(Packet packet) {
        System.out.println("Sending packet " +packet.getType().toString() + " to " + this.toString());
        try {
            oos.writeObject(packet);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnect from client, close thread
     */
    public void disconnect() {
        dbs.disconnectClient(this);
        active = false;
    }

    /**
     * Returns reference to input stream
     * @return  object input stream
     */
    public ObjectInputStream getOIS() {
        return ois;
    }

    /**
     * Returns reference to output stream
     * @return  object output stream
     */
    public ObjectOutputStream getOOS() {
        return oos;
    }

}
