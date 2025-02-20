package ee.taltech.game.shared.packet;

import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PlayerJoinedLobbyPacket {

    private Player player;
    private Lobby lobby;
}
