package ee.taltech.game.server.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.game.server.ServerLauncher;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.GetLobbiesPacket;
import ee.taltech.game.shared.packet.JoinLobbyPacket;
import ee.taltech.game.shared.packet.LeaveLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;


public class ServerListener implements Listener {

    @Override
    public void received(Connection connection, Object object) {
        switch (object) {
            case RegisterPlayerPacket packet ->
                ServerLauncher.getInstance().getGame().registerPlayer(connection.getID(), packet.getName());
            case CreateLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().createLobby(connection.getID());
            case LeaveLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().leaveLobby(connection.getID(), packet.getId());
            case GetLobbiesPacket packet ->
                ServerLauncher.getInstance().getGame().getLobbies(connection.getID());
            case JoinLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().joinLobby(connection.getID(), packet.getLobbyId());
            default ->
                // TODO : ADD LOGGER?
                System.out.println("PACKET SKIPPED");
        }
    }
}
