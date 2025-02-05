package ee.taltech.game.listener.button;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ee.taltech.game.Main;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RegisterPlayerClickListener extends ClickListener {

    private TextField field;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        String username = field.getText();
        if (username.isBlank()) return;
        Main.getInstance().registerPlayer(username);
    }
}
