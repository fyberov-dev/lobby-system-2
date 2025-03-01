package ee.taltech.game.server;

import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import ee.taltech.game.server.listener.ServerListener;
import ee.taltech.game.server.system.Game;
import ee.taltech.game.shared.util.KryoHelper;
import lombok.Getter;

import java.io.IOException;

import static ee.taltech.game.shared.util.Constants.DEFAULT_TCP_PORT;
import static ee.taltech.game.shared.util.Constants.DEFAULT_UDP_PORT;

@Getter
public class ServerLauncher extends Server {

    @Getter
    private static ServerLauncher instance;
    private final Game game;

    public ServerLauncher() {
        instance = this;
        KryoHelper.registerClasses(getKryo());
        addListener(new ServerListener());
        this.game = new Game();
    }

    private void startServer() {
        start();
        try {
            bind(DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
            Log.info(String.format("Server is up and using %d port for tcp and %d for udp", DEFAULT_TCP_PORT, DEFAULT_UDP_PORT));
        } catch (IOException e) {
            Log.info("SOMETHING BAD HAPPENED");
        }
    }

    public static void main(String[] args) {
        new ServerLauncher().startServer();
    }
}
