# Guide for the Lobby System on LibGDX

## 1. Introduction

This is a basic guide how to create the lobby system using LibGDX on the client and Kryonet on the server.

## 2. User register

### 2.1. Create registration form

First of all, on client you need to add a new package ```screen``` and add new class ```MainScreen.java```.
This screen will be dedicated to register the player.
Create new stage and add 3 actors to this (Label, TextField and TextLabel).

_MainScreen.java_
```java
@Override
public void show() {
    Stage stage = new Stage(); // Create new stage
    Gdx.input.setInputProcessor(stage); // Make buttons clickable
    Label label = new Label("Write your name:", skin); // Create Label
    TextField field = new TextField("", skin); // Create TextField
    TextButton button = new TextButton("START", skin); // Create TextButton
    button.addListener(new RegisterPlayerClickListener(field.getName())); // Add click listener on button
    Table table = new Table(); // Create new table
    table.setFillParent(true); // Make it centered
    table.defaults().space(10); // Add spacing 10 px
    table.add(label).uniform().fillX(); // Add label to the table
    table.row(); // move to the next row
    table.add(field); // add field to the table
    table.row(); // move to the next row
    table.add(button).uniform().fillX(); // add button to the table

    stage.addActor(table); // add table to the stage
}

@Override
public void render(float delta) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // clear the screen
    Gdx.gl.glClearColor(0.157f, 0.196f, 0.522f, 1); // Set blue background
    stage.act(delta); // updated all actors
    stage.draw(); // render actors on the stage
}
```

### 2.2. Send UDP request to the server

Before sending our request to the server we need to connect to the server.

#### 2.2.1. ClientLauncher

```java
public class ClientLauncher extends Client {

    private static ClientLauncher instance;

    private ClientLauncher() {
        KryoHelper.registerClasses(getKryo());
        connectToServer();
    }

    public static ClientLauncher getInstance() {
        if (instance == null) {
            instance = new ClientLauncher();
        }
        return instance;
    }

    public void connectToServer() {
        start();
        try {
            connect(DEFAULT_CONNECTION_TO_SERVER_TIMEOUT, DEFAULT_HOST, DEFAULT_TCP_PORT, DEFAULT_UDP_PORT);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
```

*After using `getInstance` method for the first time, it will automatically connect to the server.

#### 2.2.2 Constants

Create `Constants` class in the shared folder, to hold constant variables

_Constants.java_
```java
public class Constants {

    public static final int DEFAULT_CONNECTION_TO_SERVER_TIMEOUT = 5000;
    public static final int DEFAULT_TCP_PORT = 8080;
    public static final int DEFAULT_UDP_PORT = 8081;
    public static final String DEFAULT_HOST = "localhost";
}
```

### 2.3. Add click listener to the button

Now add a new class `RegisterPlayerClickListener` that will extend ```ClickListener```.
It will have a constructor that takes in `TextField field`.

_RegisterPlayerClickListener.java_
```java
@AllArgsConstructor
public class RegisterPlayerClickListener extends ClickListener {

    private TextField field;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        String username = field.getText();
        if (username.isBlank()) return;
        ClientLauncher.getInstance().sendUDP(new RegisterPlayerPacket(username));
    }
}
```

After you connected to the server UDP request is sent.

If you try it now, you will get an error. To fix that error you need to register ```RegisterPlayerPacket``` class using Kryo.
To do it better. Create `KryoHelper` class in the shared folder .

_KryoHelper.java_
```java
public class KryoHelper {

    public static void registerClasses(Kryo kryo) {
        kryo.register(RegisterPlayerPacket.class);
        // Register more classes here.
    }
}
```

Use it on the server and on the client

_ClientLauncher.java_
```java
private ClientLauncher() {
    KryoHelper.registerClasses(getKryo());
    ...
}
```

_ServerLauncher.java_
```java
public ServerLauncher() {
    KryoHelper.registerClasses(getKryo());
    ...
}
```

The class will be registered on both the server and client.


#### 2.4. Lombok

For this guide I am using [lombok](https://projectlombok.org/).

To add the lombok you need to open ```build.gradle``` file and add next lines to your dependencies.

_build.gradle_
```gradle
annotationProcessor "org.projectlombok:lombok:1.18.34"
compileOnly "org.projectlombok:lombok:1.18.34"
```
_*Update version if needed_

### 2.5. Request handling on the server

Add new ```ServerListener``` on the server and start listening it.

_ServerLauncher.java_
```java
public ServerLauncher() {
    ...
    addListener(new ServerListener());
}
```

And now handle the request from the client.

_ServerListener.java_
```java
public class ServerListener implements Listener {

    @Override
    public void received(Connection connection, Object object) {
        switch (object) {
            case RegisterPlayerPacket packet -> // if object is instance of RegisterPlayerPacket
                ServerLauncher.getInstance().registerPlayer(connection.getID(), packet.getName());
            default ->
                // leave it like this
                System.out.println("PACKET SKIPPED");
        }
    }
}
```

_ServerLauncher.java_
```java
public void registerPlayer(int id, String name) {
    Player player = new Player(id, name);
    players.put(id, player);
    sendToUDP(id, new RegisterPlayerPacket(name));
}
```

And create our `Player` class (I have it in the shared package).

_Player.java_
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Player {

    private int id;
    private String name;
}
```


### 2.7. Request handling on the client

First of all, add listener to the client and handle ```RegisterPlayerPacket```.

_ClientListener.java_
```java
public class ClientListener implements Listener {

    @Override
    public void received(Connection connection, Object object) {
        switch (object) {
            case RegisterPlayerPacket packet ->
                Main.getInstance().createPlayer(connection.getID(), packet.getName());
            default -> System.out.println("Skipped package");
        }
    }
}
```

After that save newly created player and switch the screen to the ```LobbiesListScreen```.

_Main.java_
```java
private Player currentPlayer;

public void createPlayer(int id, String username) {
    currentPlayer = new Player(id, username);
    Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
}
```

### 2.8. Use Runnable for changing screen

_SetScreenRunnable.java_
```java
@AllArgsConstructor
public class SetScreenRunnable implements Runnable {

    private Screen screen;

    @Override
    public void run() {
        Main.getInstance().setScreen(screen);
    }
}
```
*You need to use Runnable because the game and the kryonet connection are working on different Threads.

## 3. Create Lobby

### 3.1. Add lobby class

Next step is add possibility to create lobbies.
First of all, we will create the `Lobby` class.

_Lobby.java_
```java
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

    /**
     * @param player creator name to show in the name of the lobby
     * @return string in format "{playerName}'s lobby"
     */
    private String generateLobbyName(Player player) {
        return String.format("%s's lobby", player.getName());
    }
}
```

I put it in the shared folder

### 3.2. Add click listener to the button

_LobbiesListScreen.java_

```java
TextButton addLobbyButton = new TextButton("Add Lobby", skin);
addLobbyButton.addListener(new CreateLobbyClickListener());
```

_CreateLobbyClickListener.java_
```java
public class CreateLobbyClickListener extends ClickListener {

    @Override
    public void clicked(InputEvent event, float x, float y) {
        ClientLauncher.getInstance().sendUDP(new CreateLobbyPacket());
    }
}
```

Send just empty ```CreateLobbyPacket``` class because when we create the lobby we only need the player ID.
Player ID we will get with the request.

_CreateLobbyPacket.java_

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateLobbyPacket {

    private Lobby lobby;
}
```

*hold lobby for the response from the server

### 3.3. Handle the request on the server

_ServerListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case CreateLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().createLobby(connection.getID());
        ...
    }
}
```

We need to add new map of lobbies to the server where we will store all the lobbies.

_Game.java_
```java
private Map<Integer, Lobby> lobbies = new HashMap<>();

public void createLobby(int id) {
    Player player = players.get(id);
    Lobby lobby = new Lobby(player);
    lobbies.put(lobby.getId(), lobby);
    ServerLauncher.getInstance().sendToUDP(id, new CreateLobbyPacket(lobby));
}
```

### 3.4. Handle the request on the client

_ClientListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case CreateLobbyPacket packet ->
                Main.getInstance().joinLobby(Main.getInstance().getCurrentPlayer(), packet.getLobby());
        ...
    }
}
```

_Main.java_
```java
public void joinLobby(Lobby lobby) {
    currentLobby = lobby;
    Gdx.app.postRunnable(new SetScreenRunnable(new LobbyScreen(currentLobby)));
}
```

## 4. Lobby Leaving

Implementation of leaving the lobby should be similar to the implementation of creating the lobby

### 4.1. Add click listener to the button

_LobbyScreen.java_
```java
TextButton backButton = new TextButton("", skin);
backButton.addListener(new LeaveLobbyClickListener(lobby.getId()));
```

_LeaveLobbyClickListener.java_
```java
@AllArgsConstructor
public class LeaveLobbyClickListener extends ClickListener {

    private int id;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        ClientLauncher.getInstance().sendUDP(new LeaveLobbyPacket(id));
    }
}
```

### 4.2. Handle click on the button

_Main.java_
```java
public void removePlayer(int id) {
    currentLobby = null;
    Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
}
```

### 4.3. Handle Packet on the server

_ServerListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case LeaveLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().leaveLobby(connection.getID(), packet.getId());
        ...
    }
}
```

_Game.java_
```java
public void leaveLobby(int playerId, int lobbyId) {
    Lobby lobby = lobbies.get(lobbyId);
    lobby.kickPlayer(playerId);
    if (lobby.getPlayersNumber() == 0) {
        lobbies.remove(lobbyId);
    }
    ServerLauncher.getInstance().sendToUDP(playerId, new LeaveLobbyPacket(playerId));
}
```

### 4.4. Update Lobby class

_Lobby.java_
```java
public void kickPlayer(int playerId) {
    players.remove(playerId);
}

public int getPlayersNumber() {
    return players.keySet().size();
}
```

It is not necessary to send anything back to the client, because you are already leaving the lobby

## 5. Show lobbies

### 5.1. Hold variables 

To show lobbies you need to hold next variables:

_LobbiesListScreen.java_
```java
private Table lobbies;
private Map<Integer, Table> lobbyActors = new HashMap<>();
```

Lobbies table will have all the lobby Actors in it.
And the map `lobbyActors` is needed to easily find the actor and delete it in the future.

### 5.2. Request lobbies

When you are getting to the `LobbiesListScreen` after `show` method is executed request to the server is sent.

_LobbiesListScreen.java_
```java
@Override
protected void createInterface() {
    ...
    ClientLauncher.getInstance().sendUDP(new GetLobbiesPacket());
}
```

_GetLobbiesPacket.java_
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetLobbiesPacket implements Packet {
    
    private Map<Integer, Lobby> lobbies;
}
```

When you are sending request to the server you can leave the packet empty. As the response you get packet with the lobbies list.

### 5.3. Handle the request on the server

_ServerListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case GetLobbiesPacket packet ->
                ServerLauncher.getInstance().getGame().getLobbies(connection.getID());
        ...
    }
}
```

_Game.java_
```java
public void getLobbies(int id) {
    ServerLauncher.getInstance().sendToUDP(id, new GetLobbiesPacket(lobbies));
}
```

### 5.4. Update existing code

Now we can add some changes to our existing code.\
If lobby is created -> send lobbies

_Game.java_
```java
public void createLobby(int id) {
    ...
    ServerLauncher.getInstance().sendToAllExceptUDP(id, new GetLobbiesPacket(lobbies));
}
```

*To optimize you can send this packet only to those people who are searching for the lobby. 
To do this you can add to the `Player` class new variable, for example `state` and set it as the `State.SEARCHING`.
After just iterate all the players and send lobbies to the people with `State.SEARCHING`

### 5.5. Handle request on the client

_ClientListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case GetLobbiesPacket packet ->
                Main.getInstance().updateLobbies(packet.getLobbies());
        ...
    }
}
```

First of all you need to check if the player is in the `LobbiesListScreen` to prevent exceptions.
Then add all the lobbies to the screen.

_Main.java_
```java
public void updateLobbies(Map<Integer, Lobby> lobbies) {
    if (getScreen() instanceof LobbiesListScreen lobbiesListScreen) {
        lobbiesListScreen.clearLobbies();
        for (Lobby lobby : lobbies.values()) {
            lobbiesListScreen.addLobby(lobby);
        }
    }
}
```

_LobbiesListScreen.java_
```java
private Table lobbies;
private final Map<Integer, Table> lobbyActors = new HashMap<>();

public void addLobby(Lobby lobby) {
    TextButton lobbyButton = new TextButton(lobby.getName(), skin);
    lobbies.add(lobbyButton).expandX().fillX();
    lobbies.row();
    lobbyActors.put(lobby.getId(), lobbyButton);
}

public void removeLobby(int lobbyId) {
    Table lobby = lobbyActors.remove(lobbyId);
    lobbies.removeActor(lobby);
}

public void clearLobbies() {
    lobbies.clear();
    lobbyActors.clear();
}
```

## 6. Delete lobby from list

### 6.1 Update existing code

When in the lobby is 0 players it will be deleted and after this you need to delete this lobby from lobbies list.

_Game.java_
```java
public void leaveLobby(int playerId, int lobbyId) {
    ...
    if (lobby.getPlayersNumber() == 0) {
        ...
        ServerLauncher.getInstance().sendToAllExceptUDP(playerId, new DeleteLobbyPacket(lobbyId));
    }
}
```

### 6.2 Handle request on the client

_ClientListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case DeleteLobbyPacket packet ->
                Main.getInstance().deleteLobby(packet.getLobbyId());
        ...
    }
}
```

_Main.java_
```java
public void deleteLobby(int lobbyId) {
    if (getScreen() instanceof LobbiesListScreen lobbiesListScreen) {
        lobbiesListScreen.removeLobby(lobbyId);
    }
}
```

After this steps lobby will disappear from the list.

## 7. Join lobby

### 7.1 Add click listeners to the lobbies

_JoinLobbyClickListener.java_
```java
@AllArgsConstructor
public class JoinLobbyClickListener extends ClickListener {

    private final int lobbyId;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        ClientLauncher.getInstance().sendUDP(new JoinLobbyPacket(lobbyId));
    }
}
```

Add to every lobby button click listener and set on every button unique id.

_LobbiesListScreen.java_
```java
public void addLobby(Lobby lobby) {
    TextButton lobbyButton = new TextButton(lobby.getName(), skin);
    lobbyButton.addListener(new JoinLobbyClickListener(lobby.getId()));
    ...
}
```

### 7.2 Handle request on the server

_ServerListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case JoinLobbyPacket packet ->
                ServerLauncher.getInstance().getGame().joinLobby(connection.getID(), packet.getLobbyId());
        ...
    }
}
```

Add player to the lobby and send to every player in the lobby that player have joined.

_Game.java_
```java
public void joinLobby(int id, int lobbyId) {
    Player player = players.get(id);
    Lobby lobby = lobbies.get(lobbyId);
    lobby.joinLobby(player);
    for (Player currentPlayer : lobby.getPlayers().values()) {
        ServerLauncher.getInstance().sendToUDP(currentPlayer.getId(), new PlayerJoinedLobbyPacket(player, lobby));
    }
}
```

_PlayerJoinedLobbyPacket.java_
```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PlayerJoinedLobbyPacket {

    private Player player;
    private Lobby lobby;
}
```

### 7.3 Handle response on the client

#### 7.3.1. Handle response

_ClientListener.java_
```java
@Override
public void received(Connection connection, Object object) {
    switch (object) {
        ....
        case PlayerJoinedLobbyPacket packet ->
                Main.getInstance().joinLobby(packet.getPlayer(), packet.getLobby());
        ...
    }
}
```

#### 7.3.2. Update existing code

_Main.java_
```java
public void joinLobby(Player player, Lobby lobby) {
    if (player.getId() == currentPlayer.getId()) {
        currentLobby = lobby;
        Gdx.app.postRunnable(new SetScreenRunnable(new LobbyScreen(currentLobby)));
    } else {
        if (getScreen() instanceof LobbyScreen lobbyScreen) {
            lobbyScreen.addPlayer(player);
        }
    }
}
```

_LobbyScreen.java_
```java
private final Map<Integer, Actor> players = new HashMap<>();

public void addPlayer(Player player) {
    Label playerNameLabel = new Label(player.getName(), skin);
    playersTable.add(playerNameLabel).expandX().fillX();
    players.put(player.getId(), playerNameLabel);
    playersTable.row();
}
```

And when player joined the lobby, you need to add all existing players to the lobby interface

_LobbyScreen.java_
```java
@Override
protected void createInterface() {
    ...
    for (Player player : lobby.getPlayers().values()) {
        addPlayer(player);
    }
}
```

## 8. Remove left player from the lobby

### 8.1. Update existing code on the server

_Game.java_
```java
public void leaveLobby(int playerId, int lobbyId) {
    ...
    if (lobby.getPlayersNumber() == 0) {
        ...
    }
    ServerLauncher.getInstance().sendToAllUDP(new LeaveLobbyPacket(playerId));
}
```

So now, if all the players leave the lobby, it will be deleted. Otherwise, player will be removed from the lobby interface of other players

### 8.2. Handle response from the server

_ClientListener.java_
```java
    @Override
public void received(Connection connection, Object object) {
    switch (object) {
        ...
        case LeaveLobbyPacket packet ->
                Main.getInstance().removePlayer(packet.getId());
        ...
    }
}
```

_Main.java_
```java
public void removePlayer(int id) {
    if (id == ClientLauncher.getInstance().getID()) {
        currentLobby = null;
        Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
    } else {
        if (getScreen() instanceof LobbyScreen lobbyScreen) {
            lobbyScreen.removePlayer(id);
        }
    }
}
```

_LobbyScreen.java_
```java
public void removePlayer(int id) {
    Actor player = players.remove(id);
    playersTable.removeActor(player);
}
```
