package ee.taltech.game.listener.button;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import ee.taltech.game.Main;
import ee.taltech.game.shared.packet.JoinLobbyPacket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JoinLobbyClickListener extends ClickListener {

    private final int lobbyId;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        Main.getInstance().getClient().sendUDP(new JoinLobbyPacket(lobbyId));
    }
}
