package ee.taltech.game.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.listener.ServerListener;
import ee.taltech.game.server.system.Game;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;

@Getter
public class ServerLauncher extends Server {

    @Getter
    private static ServerLauncher instance;
    private final Game game;

    public ServerLauncher() {
        instance = this;
        registerKryo();
        addListener(new ServerListener());
        this.game = new Game();
    }

    private void registerKryo() {
        Kryo kryo = getKryo();

        // TODO : Register classes
        kryo.register(HashMap.class);

        kryo.register(RegisterPlayerPacket.class);
        kryo.register(CreateLobbyPacket.class);
        kryo.register(Lobby.class);
        kryo.register(Player.class);
    }

    private static final int DEFAULT_TCP_PORT = 8080;
    private static final int DEFAULT_UDP_PORT = 8081;

    private void startServer() {
        start();
        try {
            bind(DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
        } catch (IOException e) {
            // TODO : ADD LOGGER??
            System.out.println("SOMETHING BAD HAPPENED");
        }
    }

    public static void main(String[] args) {
        new ServerLauncher().startServer();
    }
}
