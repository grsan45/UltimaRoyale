package ml.matrixdevteam.ultimaroyale.arena;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arenas {
    private static HashMap<String, Arena> arenas = new HashMap<>();
    private static HashMap<String, Arena> playerArenaBTB = new HashMap<>();
    private static List<Arena> list = new ArrayList<>();

    public static Arena getArena(final String arenaName) {
        if (arenas.containsKey(arenaName)) {
            final Arena arena = arenas.get(arenaName);
            return arena;
        }

        return null;
    }

    public static HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public static boolean isInArena(final Player player) {
        return playerArenaBTB.containsKey(player.getName());
    }

    public static void removeArena(final Player player) {
        playerArenaBTB.remove(player.getName());
    }

    public static void addArena(final Arena arena) {
        if (!arenas.containsKey(arena.getName())) {
            arenas.put(arena.getName(), arena);
            if (!list.contains(arena)) {
                list.add(arena);
            }
        }
    }

    public static void addArena(final Player player, final Arena arena) {
        if (!playerArenaBTB.containsKey(player.getName())) {
            playerArenaBTB.put(player.getName(), arena);
        }
    }

    public static Arena getArena(final Player player) {
        if (playerArenaBTB.containsKey(player.getName())) {
            final Arena arena = playerArenaBTB.get(player.getName());
            return arena;
        }

        return null;
    }

    public static Boolean arenaExists(final String arenaName) {
        return arenas.containsKey(arenaName);
    }
}
