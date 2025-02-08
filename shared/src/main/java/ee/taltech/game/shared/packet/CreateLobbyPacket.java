package ee.taltech.game.shared.packet;

import ee.taltech.game.shared.lobby.Lobby;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateLobbyPacket {

    private Lobby lobby;
}
