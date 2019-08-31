package ml.matrixdevteam.ultimaroyale.listeners;

import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import ml.matrixdevteam.ultimaroyale.arena.Arenas;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class FallListener implements Listener {
    UltimaRoyale plugin;

    public FallListener(final UltimaRoyale plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void voidListener(final PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline()) {
            final Location loc = player.getLocation();
            if (loc.getBlockY() == 0) {
                player.setHealth(0.0);
            }
        }
    }
}
