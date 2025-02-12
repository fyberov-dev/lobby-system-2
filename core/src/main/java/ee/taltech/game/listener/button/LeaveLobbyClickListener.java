package ee.taltech.game.listener.button;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ee.taltech.game.Main;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LeaveLobbyClickListener extends ClickListener {

    private int id;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        Main.getInstance().leaveLobby(id);
    }
}
