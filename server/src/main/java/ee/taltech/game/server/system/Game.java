package ee.taltech.game.server.system;

import ee.taltech.game.server.ServerLauncher;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private Map<Integer, Player> players = new HashMap<>();
    private Map<Integer, String> lobbies = new HashMap<>();

    public void registerPlayer(int id, String name) {
        Player player = new Player(id, name);
        players.put(id, player);
        ServerLauncher.getInstance().sendToUDP(id, new RegisterPlayerPacket(name));
    }
}
