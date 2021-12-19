package swd.team11.coviddatabase.server.network;

import swd.team11.coviddatabase.GuidelinesInfo;
import swd.team11.coviddatabase.VaccinationInfo;
import swd.team11.coviddatabase.events.MySQLQueryEvent;
import swd.team11.coviddatabase.server.mysql.Table;
import swd.team11.coviddatabase.utils.Packet;
import swd.team11.coviddatabase.utils.PacketType;

import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Class for handling sent vaccination info
 */
public class SendDataHandler {

    Packet packet;
    ClientHandler client;
    DatabaseServer dbs;

    public SendDataHandler(Packet packet, ClientHandler client, DatabaseServer dbs) {
        this.packet = packet;
        this.client = client;
        this.dbs = dbs;
    }

    public void processPacket() {
        if (packet.getType().equals(PacketType.SEND_VACCINATION_INFO)) {
            VaccinationInfo vaccinationInfo = (VaccinationInfo) packet.getData();
            Table vacInfoTable = dbs.getTable("user_vaccination_dates");

            HashMap<String, String> columnValues = new HashMap<>();
            columnValues.put("vaccination_name", "'"+vaccinationInfo.getName()+"'");
            columnValues.put("date_received", "STR_TO_DATE('"+vaccinationInfo.getDateReceived().toString()+"', '%Y-%m-%d')");
            columnValues.put("location", "'"+vaccinationInfo.getLocation()+"'");

            try {
                vacInfoTable.addRow(new MySQLQueryEvent() {
                    @Override
                    public void querySentEvent(Packet response) {
                        client.sendPacket(response);
                    }

                    @Override
                    public void resultReceivedEvent(ResultSet rs) {

                    }
                }, dbs.getQueryBuffer(), vaccinationInfo.getUsername(), columnValues);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.sendPacket(new Packet(PacketType.ERROR, "Unable to add vaccination status at this time"));
            }
        } else if (packet.getType().equals(PacketType.UPDATE_GUIDELINES)) {
            GuidelinesInfo newGuidelines = (GuidelinesInfo) packet.getData();
            Table guidelinesTable = dbs.getTable("organization_guidelines");
            try {
                guidelinesTable.updateColumnInRow(new MySQLQueryEvent() {
                    @Override
                    public void querySentEvent(Packet response) {
                        client.sendPacket(response);
                    }

                    @Override
                    public void resultReceivedEvent(ResultSet rs) {

                    }
                }, dbs.getQueryBuffer(), newGuidelines.getUsername(), "policy", newGuidelines.getGuideline());
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.sendPacket(new Packet(PacketType.ERROR, "Unable to update policy at this time"));
            }

        } else {
            client.sendPacket(new Packet(PacketType.ERROR, "Expected vaccination or guidelines info, received wrong type"));
        }
    }

}
