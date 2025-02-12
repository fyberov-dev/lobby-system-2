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

### 2.2. Add click listener to the button

Now add a new class ```RegisterPlayerClickListener``` that will extend ```ClickListener```.
It will have a constructor that takes in string ```username```.

_RegisterPlayerClickListener.java_
```java
@AllArgsConstructor
public class RegisterPlayerClickListener extends ClickListener {

    private String username;

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (username.isBlank()) return;
        Client.getInstance().registerPlayer(username);
    }
}
```

### 2.3. Lombok (Additional)

For this guide I am using [lombok](https://projectlombok.org/).

To add the lombok you need to open ```build.gradle``` file and add next lines to your dependencies.

_build.gradle_
```gradle
annotationProcessor "org.projectlombok:lombok:1.18.34"
compileOnly "org.projectlombok:lombok:1.18.34"
```
_*Update version if needed_

### 2.4. Instance (Additional)

And I am using ```instance``` to refer to the main class.

_Main.java_
```java
@Getter
private static Client instance;

public Client() {
    instance = this;
}
```

### 2.5. Send UDP request to the server

Before sending our request to the server we need to connect to the server.

After you connected to the server send UDP request.

_Main.java_
```java
public void registerPlayer(String name) {
    client = connectToServer(); // connect to the server and return kryonet Client object
    client.sendUDP(new RegisterPlayerPacket(name));
}
```

If you try it now, you will get an error. To fix that error you need to register ```RegisterPlayerPacket``` class using Kryo.

_ClientLauncher.java_
```java
private void registerKryo() {
    Kryo kryo = getKryo();

    // register classes below
    kryo.register(RegisterPlayerPacket.class);
}
```
*The class must be registered on both the server and the client.

### 2.6. Request handling on the server

Add new ```ServerListener``` on the server and start listening it.

_ServerLauncher.java_
```java
public ServerLauncher() {
    instance = this;
    registerKryo();
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

And our `Player` class (I have it in the shared package).

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
        Main.getInstance().createLobby();
    }
}
```

Send just empty ```CreateLobbyPacket``` class because when we create the lobby we only need the player ID.
Player ID we will get with the request.

_Main.java_
```java
public void createLobby() {
    client.sendUDP(new CreateLobbyPacket());
}
```

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
case CreateLobbyPacket packet ->
    ServerLauncher.getInstance().getGame().createLobby(connection.getID());
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
case CreateLobbyPacket packet ->
    Main.getInstance().joinLobby(packet.getLobby());
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
        Main.getInstance().leaveLobby(id);
    }
}
```

### 4.2. Handle click on the button

_Main.java_
```java
public void leaveLobby(int id) {
    client.sendUDP(new LeaveLobbyPacket(id));
    currentLobby = null; // clear lobby locally
    Gdx.app.postRunnable(new SetScreenRunnable(new LobbiesListScreen()));
}
```

### 4.3. Handle Packet on the server

_ServerListener.java_
```java
case LeaveLobbyPacket packet ->
    ServerLauncher.getInstance().getGame().leaveLobby(connection.getID(), packet.getId());
```

_Game.java_
```java
public void leaveLobby(int playerId, int lobbyId) {
    Lobby lobby = lobbies.get(lobbyId);
    lobby.kickPlayer(playerId);
    if (lobby.getPlayersNumber() == 0) {
        lobbies.remove(lobbyId);
    }
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










