package ee.taltech.game.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ee.taltech.game.Main;
import ee.taltech.game.listener.button.CreateLobbyClickListener;
import ee.taltech.game.listener.button.JoinLobbyClickListener;
import ee.taltech.game.shared.lobby.Lobby;

import java.util.HashMap;
import java.util.Map;

public class LobbiesListScreen extends Screen {

    private Table lobbies;
    private final Map<Integer, Table> lobbyActors = new HashMap<>();

    @Override
    protected void createInterface() {
        Label label = new Label("Lobbies", skin);
        TextButton addLobbyButton = new TextButton("Add Lobby", skin);
        addLobbyButton.addListener(new CreateLobbyClickListener());
        Table table = new Table();
        lobbies = new Table();
        table.setFillParent(true);
        table.defaults().space(10);
        table.add(label);
        table.add(addLobbyButton);
        table.row();
        table.add(lobbies).colspan(2).fillX();

        stage.addActor(table);

        Main.getInstance().getLobbies();
    }

    public void addLobby(Lobby lobby) {
        TextButton lobbyButton = new TextButton(lobby.getName(), skin);
        lobbyButton.addListener(new JoinLobbyClickListener(lobby.getId()));
        lobbies.add(lobbyButton).expandX().fillX();
        lobbies.row();
        lobbyActors.put(lobby.getId(), lobbyButton);
    }

    public void removeLobby(int lobbyId) {
        Table lobby = lobbyActors.remove(lobbyId);
        lobbies.removeActor(lobby);
    }
}
