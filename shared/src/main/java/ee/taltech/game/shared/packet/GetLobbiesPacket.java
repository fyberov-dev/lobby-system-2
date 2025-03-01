package ee.taltech.game.shared.packet;

import ee.taltech.game.shared.lobby.Lobby;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetLobbiesPacket implements Packet {

    private Map<Integer, Lobby> lobbies;
}
