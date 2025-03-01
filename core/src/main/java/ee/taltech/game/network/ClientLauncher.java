package ee.taltech.game.network;

import com.esotericsoftware.kryonet.Client;
import ee.taltech.game.listener.ClientListener;
import ee.taltech.game.shared.util.KryoHelper;

import java.io.IOException;

import static ee.taltech.game.shared.util.Constants.DEFAULT_CONNECTION_TO_SERVER_TIMEOUT;
import static ee.taltech.game.shared.util.Constants.DEFAULT_HOST;
import static ee.taltech.game.shared.util.Constants.DEFAULT_TCP_PORT;
import static ee.taltech.game.shared.util.Constants.DEFAULT_UDP_PORT;

public class ClientLauncher extends Client {

    private static ClientLauncher instance;

    private ClientLauncher() {
        addListener(new ClientListener());
        KryoHelper.registerClasses(getKryo());
        connectToServer();
    }

    public static ClientLauncher getInstance() {
        if (instance == null) {
            instance = new ClientLauncher();
        }
        return instance;
    }

    public void connectToServer() {
        start();
        try {
            connect(DEFAULT_CONNECTION_TO_SERVER_TIMEOUT, DEFAULT_HOST, DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
        } catch (IOException e) {
            // TODO : Add logger?
            System.out.println(e);
        }
    }
}
