package ee.taltech.game.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LobbiesListScreen extends Screen {

    private Table lobbies;

    @Override
    void createInterface() {
        Label label = new Label("Lobbies", skin);
        TextButton addLobbyButton = new TextButton("Add Lobby", skin);
        Table table = new Table();
        lobbies = new Table();
        table.setFillParent(true);
        table.defaults().space(10);
        table.add(label);
        table.add(addLobbyButton);
        table.row();
        table.add(lobbies).colspan(2).fillX();

        addLobby();

        stage.addActor(table);
    }

    private void addLobby() {
        TextButton lobby = new TextButton("123's lobby", skin);
        lobbies.add(lobby).expandX().fillX();
    }
}
