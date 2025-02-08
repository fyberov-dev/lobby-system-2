package ee.taltech.game.runnable;

import com.badlogic.gdx.Screen;
import ee.taltech.game.Main;
import ee.taltech.game.network.ClientLauncher;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SetScreenRunnable implements Runnable {

    private Screen screen;

    @Override
    public void run() {
        Main.getInstance().setScreen(screen);
    }
}
