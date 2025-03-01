package ee.taltech.game.listener.button;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ee.taltech.game.network.ClientLauncher;
import ee.taltech.game.shared.packet.CreateLobbyPacket;

public class CreateLobbyClickListener extends ClickListener {

    @Override
    public void clicked(InputEvent event, float x, float y) {
        ClientLauncher.getInstance().sendUDP(new CreateLobbyPacket());
    }
}
