package ee.taltech.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FillViewport;

abstract class Screen extends ScreenAdapter {

    protected Stage stage;
    protected Skin skin;

    private static final float SCALING_FACTOR = 3.5f;

    @Override
    public void show() {
        stage = new Stage(new FillViewport(Gdx.graphics.getWidth() / SCALING_FACTOR, Gdx.graphics.getHeight() / SCALING_FACTOR));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        createInterface();
    }

    abstract void createInterface();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.157f, 0.196f, 0.522f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
