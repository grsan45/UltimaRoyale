package ml.matrixdevteam.ultimaroyale.arena;

public enum LeaveReason {
    QUIT("QUIT", 0),
    COMMAND("COMMAND", 1),
    DEATHS("DEATHS", 2),
    KICK("KICK", 3),
    STOPPED("STOPPED", 4);

    LeaveReason(final String s, final int n) {
    }
}