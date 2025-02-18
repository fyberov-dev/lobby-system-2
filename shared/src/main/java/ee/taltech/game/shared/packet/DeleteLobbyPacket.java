package ee.taltech.game.shared.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteLobbyPacket implements Packet {

    private int lobbyId;
}
