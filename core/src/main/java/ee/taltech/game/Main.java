package ee.taltech.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import ee.taltech.game.network.ClientLauncher;
import ee.taltech.game.runnable.SetScreenRunnable;
import ee.taltech.game.screen.LobbiesListScreen;
import ee.taltech.game.screen.LobbyScreen;
import ee.taltech.game.screen.MainScreen;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.LeaveLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;
import lombok.Getter;

@Getter
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Getter
    private static Main instance;
    private ClientLauncher client = null;
    private Player currentPlayer;
    private Lobby currentLobby;

    public Main() {
        instance = this;
    }

    @Override
    public void create() {
        setScreen(new MainScreen());
    }

    public void registerPlayer(String name) {
        client = connectToServer();
        client.sendUDP(new RegisterPlayerPacket(name));
    }

    public void createPlayer(int id, String username) {
        currentPlayer = new Player(id, username);
        Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
    }

    public void createLobby() {
        client.sendUDP(new CreateLobbyPacket());
    }

    public void joinLobby(Lobby lobby) {
        currentLobby = lobby;
        Gdx.app.postRunnable(new SetScreenRunnable(new LobbyScreen(currentLobby)));
    }

    public void leaveLobby(int id) {
        client.sendUDP(new LeaveLobbyPacket(id));
        currentLobby = null;
        Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
    }

    private static final String LOCALHOST = "127.0.0.1";

    private ClientLauncher connectToServer() {
        if (client == null) {
            client = new ClientLauncher(LOCALHOST);
            client.connectToServer();
        }
        return client;
    }
}
