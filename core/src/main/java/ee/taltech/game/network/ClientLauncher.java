package ee.taltech.game.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import ee.taltech.game.listener.ClientListener;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.DeleteLobbyPacket;
import ee.taltech.game.shared.packet.GetLobbiesPacket;
import ee.taltech.game.shared.packet.LeaveLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;

import java.io.IOException;
import java.util.HashMap;

public class ClientLauncher extends Client {

    private String host;

    private static final int DEFAULT_CONNECTION_TO_SERVER_TIMEOUT = 5000;
    private static final int DEFAULT_TCP_PORT = 8080;
    private static final int DEFAULT_UDP_PORT = 8081;

    public ClientLauncher(String host) {
        this.host = host;
        addListener(new ClientListener());
        registerKryo();
    }

    private void registerKryo() {
        Kryo kryo = getKryo();

        // register classes below
        kryo.register(HashMap.class);

        kryo.register(RegisterPlayerPacket.class);
        kryo.register(CreateLobbyPacket.class);
        kryo.register(Lobby.class);
        kryo.register(Player.class);
        kryo.register(LeaveLobbyPacket.class);
        kryo.register(GetLobbiesPacket.class);
        kryo.register(DeleteLobbyPacket.class);
    }

    public void connectToServer() {
        start();
        try {
            connect(DEFAULT_CONNECTION_TO_SERVER_TIMEOUT, host, DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
        } catch (IOException e) {
            // TODO : Add logger?
            System.out.println(e);
        }
    }
}
