package ee.taltech.game.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import ee.taltech.game.listener.button.RegisterPlayerClickListener;

public class MainScreen extends Screen {

    @Override
    protected void createInterface() {
        Label label = new Label("Write your name:", skin);
        TextField field = new TextField("", skin);
        field.setAlignment(Align.center);
        TextButton button = new TextButton("START", skin);
        button.addListener(new RegisterPlayerClickListener(field));
        Table table = new Table();

        table.setFillParent(true);
        table.defaults().space(10);
        table.add(label).uniform().fillX();
        table.row();
        table.add(field);
        table.row();
        table.add(button).uniform().fillX();

        stage.addActor(table);
    }
}
