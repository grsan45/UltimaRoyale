package ml.matrixdevteam.ultimaroyale.arena;

public enum GameState {
    LOBBY("LOBBY", 0),
    IN_GAME("IN_GAME", 1),
    STARTING("STARTING", 2),
    STOPPING("STOPPING", 3);

    GameState(final String s, final int n) {
    }
}
