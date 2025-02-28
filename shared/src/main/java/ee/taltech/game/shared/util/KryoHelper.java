package ee.taltech.game.shared.util;

import com.esotericsoftware.kryo.Kryo;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.DeleteLobbyPacket;
import ee.taltech.game.shared.packet.GetLobbiesPacket;
import ee.taltech.game.shared.packet.JoinLobbyPacket;
import ee.taltech.game.shared.packet.LeaveLobbyPacket;
import ee.taltech.game.shared.packet.PlayerJoinedLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;

import java.util.HashMap;

public class KryoHelper {

    public static void registerClasses(Kryo kryo) {
        kryo.register(HashMap.class);
        kryo.register(RegisterPlayerPacket.class);
        kryo.register(CreateLobbyPacket.class);
        kryo.register(Lobby.class);
        kryo.register(Player.class);
        kryo.register(LeaveLobbyPacket.class);
        kryo.register(GetLobbiesPacket.class);
        kryo.register(DeleteLobbyPacket.class);
        kryo.register(JoinLobbyPacket.class);
        kryo.register(PlayerJoinedLobbyPacket.class);
    }
}
