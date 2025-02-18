package ee.taltech.game.shared.packet;

import java.util.List;
import java.util.Map;

import ee.taltech.game.shared.lobby.Lobby;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetLobbiesPacket implements Packet {


    private Map<Integer, Lobby> lobbies;
}
