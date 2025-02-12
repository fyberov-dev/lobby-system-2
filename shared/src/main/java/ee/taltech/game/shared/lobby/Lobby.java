package ee.taltech.game.shared.lobby;

import ee.taltech.game.shared.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class Lobby {

    private static int nextId = 0;

    private int id;
    private String name;
    private Map<Integer, Player> players;

    /**
     * Initialize lobby and add player to it.
     *
     * @param player creator of the lobby
     */
    public Lobby(Player player) {
        this.id = nextId++;
        this.name = generateLobbyName(player);
        this.players = new HashMap<>();
        joinLobby(player);
    }

    /**
     * @param player player to add to the lobby
     */
    public void joinLobby(Player player) {
        players.put(player.getId(), player);
    }

    public void kickPlayer(int playerId) {
        players.remove(playerId);
    }

    public int getPlayersNumber() {
        return players.keySet().size();
    }

    /**
     * @param player creator name to show in the name of the lobby
     * @return string in format "{playerName}'s lobby"
     */
    private String generateLobbyName(Player player) {
        return String.format("%s's lobby", player.getName());
    }
}
