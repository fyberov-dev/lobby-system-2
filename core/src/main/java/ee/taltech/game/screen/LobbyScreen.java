package ee.taltech.game.screen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import ee.taltech.game.listener.button.LeaveLobbyClickListener;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LobbyScreen extends Screen {

    private final Lobby lobby;
    private Table playersTable;
    private Map<Integer, Actor> players = new HashMap<>();

    @Override
    protected void createInterface() {
        stage.setDebugAll(true);
        Table table = new Table();
        table.setFillParent(true);
        TextButton backButton = createBackButton();
        backButton.addListener(new LeaveLobbyClickListener(lobby.getId()));
        Label lobbyNameLabel = new Label(lobby.getName(), skin);

        playersTable = new Table();

        table.defaults().space(25);
        table.add(backButton);
        table.add(lobbyNameLabel);
        table.row();
        table.add(playersTable).colspan(2).fillX();

        for (Player player : lobby.getPlayers().values()) {
            addPlayer(player);
        }

        stage.addActor(table);
    }

    public void addPlayer(Player player) {
        Label playerNameLabel = new Label(player.getName(), skin);
        playersTable.add(playerNameLabel).expandX().fillX();
        players.put(player.getId(), playerNameLabel);
        playersTable.row();
    }

    public void removePlayer(int id) {
        Actor player = players.remove(id);
        playersTable.removeActor(player);
    }

    private TextButton createBackButton() {
        TextButton button = new TextButton("", skin);
        SpriteDrawable drawable = createBackButtonSpriteDrawable();
        button.getStyle().up = drawable;
        button.getStyle().over = drawable;
        button.getStyle().down = drawable;
        return button;
    }

    private SpriteDrawable createBackButtonSpriteDrawable() {
        Sprite sprite = new Sprite(skin.get("tree-plus", TextureRegion.class));
        sprite.flip(true, false);
        return new SpriteDrawable(sprite);
    }
}
