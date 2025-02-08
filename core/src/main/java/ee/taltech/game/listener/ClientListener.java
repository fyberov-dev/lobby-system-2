package ee.taltech.game.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.game.Main;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;

public class ClientListener implements Listener {

    @Override
    public void received(Connection connection, Object object) {
        switch (object) {
            case RegisterPlayerPacket packet ->
                Main.getInstance().createPlayer(connection.getID(), packet.getName());
            case CreateLobbyPacket packet ->
                Main.getInstance().joinLobby(packet.getLobby());
            default -> System.out.println("Skipped package");
        }
    }
}
