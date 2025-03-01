package ee.taltech.game.server.system;

import com.esotericsoftware.minlog.Log;
import ee.taltech.game.server.ServerLauncher;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.DeleteLobbyPacket;
import ee.taltech.game.shared.packet.GetLobbiesPacket;
import ee.taltech.game.shared.packet.LeaveLobbyPacket;
import ee.taltech.game.shared.packet.PlayerJoinedLobbyPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private final Map<Integer, Player> players = new HashMap<>();
    private final Map<Integer, Lobby> lobbies = new HashMap<>();

    public void registerPlayer(int id, String name) {
        Player player = new Player(id, name);
        players.put(id, player);
        ServerLauncher.getInstance().sendToUDP(id, new RegisterPlayerPacket(name));
        Log.info(String.format("[%d]%s joined the game", player.getId(), player.getName()));
    }

    public void createLobby(int id) {
        Player player = players.get(id);
        Lobby lobby = new Lobby(player);
        lobbies.put(lobby.getId(), lobby);
        ServerLauncher.getInstance().sendToUDP(id, new CreateLobbyPacket(lobby));
        ServerLauncher.getInstance().sendToAllExceptUDP(id, new GetLobbiesPacket(lobbies));
        Log.info(String.format("%s created the lobby [%d]%s", player.getName(), lobby.getId(), lobby.getName()));
    }

    public void leaveLobby(int playerId, int lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        lobby.kickPlayer(playerId);
        if (lobby.getPlayersNumber() == 0) {
            lobbies.remove(lobbyId);
            ServerLauncher.getInstance().sendToAllExceptUDP(playerId, new DeleteLobbyPacket(lobbyId));
            Log.info(String.format("[%d]%s lobby was deleted", lobby.getId(), lobby.getName()));
        }
        ServerLauncher.getInstance().sendToAllUDP(new LeaveLobbyPacket(playerId));
        Log.info(String.format("%s left the lobby [%d]%s", players.get(playerId).getName(), lobby.getId(), lobby.getName()));
    }

    public void getLobbies(int id) {
        ServerLauncher.getInstance().sendToUDP(id, new GetLobbiesPacket(lobbies));
        Log.info(String.format("[%d] %s requested the lobbies", id, players.get(id).getName()));
    }

    public void joinLobby(int id, int lobbyId) {
        Player player = players.get(id);
        Lobby lobby = lobbies.get(lobbyId);
        lobby.joinLobby(player);
        for (Player currentPlayer : lobby.getPlayers().values()) {
            ServerLauncher.getInstance().sendToUDP(currentPlayer.getId(), new PlayerJoinedLobbyPacket(player, lobby));
        }
        Log.info(String.format("%s joined the lobby [%d]%s", player.getName(), lobby.getId(), lobby.getName()));
    }

}
