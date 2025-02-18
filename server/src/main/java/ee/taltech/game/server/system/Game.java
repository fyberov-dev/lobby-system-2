package ee.taltech.game.server.system;

import ee.taltech.game.server.ServerLauncher;
import ee.taltech.game.shared.lobby.Lobby;
import ee.taltech.game.shared.packet.CreateLobbyPacket;
import ee.taltech.game.shared.packet.DeleteLobbyPacket;
import ee.taltech.game.shared.packet.GetLobbiesPacket;
import ee.taltech.game.shared.packet.RegisterPlayerPacket;
import ee.taltech.game.shared.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Game {

    private Map<Integer, Player> players = new HashMap<>();
    private Map<Integer, Lobby> lobbies = new HashMap<>();

    public void registerPlayer(int id, String name) {
        Player player = new Player(id, name);
        players.put(id, player);
        ServerLauncher.getInstance().sendToUDP(id, new RegisterPlayerPacket(name));
    }

    public void createLobby(int id) {
        Player player = players.get(id);
        Lobby lobby = new Lobby(player);
        lobbies.put(lobby.getId(), lobby);
        ServerLauncher.getInstance().sendToUDP(id, new CreateLobbyPacket(lobby));
        ServerLauncher.getInstance().sendToAllExceptUDP(id, new GetLobbiesPacket(lobbies));
    }

    public void leaveLobby(int playerId, int lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        lobby.kickPlayer(playerId);
        if (lobby.getPlayersNumber() == 0) {
            lobbies.remove(lobbyId);
            ServerLauncher.getInstance().sendToAllExceptUDP(playerId, new DeleteLobbyPacket(lobbyId));
        }
    }

    public void getLobbies(int id) {
        ServerLauncher.getInstance().sendToUDP(id, new GetLobbiesPacket(lobbies));
    }
}
