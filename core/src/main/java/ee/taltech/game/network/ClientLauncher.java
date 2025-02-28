package ee.taltech.game.network;

import com.esotericsoftware.kryonet.Client;
import ee.taltech.game.listener.ClientListener;
import ee.taltech.game.shared.util.KryoHelper;

import java.io.IOException;

public class ClientLauncher extends Client {

    private String host;

    private static final int DEFAULT_CONNECTION_TO_SERVER_TIMEOUT = 5000;
    private static final int DEFAULT_TCP_PORT = 8080;
    private static final int DEFAULT_UDP_PORT = 8081;

    public ClientLauncher(String host) {
        this.host = host;
        addListener(new ClientListener());
        KryoHelper.registerClasses(getKryo());
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
