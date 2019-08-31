package ml.matrixdevteam.ultimaroyale.listeners;

import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import ml.matrixdevteam.ultimaroyale.arena.Arenas;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.io.File;

public class AchievementListener implements Listener {
    UltimaRoyale plugin;

    public AchievementListener(final UltimaRoyale plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline() && player.getKiller() != null) {
            final Player killer = player.getKiller();
            final String playerName = killer.getName();
            final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
            final File f = new File(userdata, File.separator + playerName + ".yml");
            final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);

            if (playerData.getInt("elims") == 1) {
                arena.sendAll(Arena.color(plugin.getConfig().getString("messages.prefix") + "&7" + killer.getName() + "&f unlocked first blood."));
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.0f);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GREEN + "Achievement Unlocked", ChatColor.WHITE + "First Blood"), 100L);
            }

            if (playerData.getInt("elims") == 5) {
                arena.sendAll(Arena.color(plugin.getConfig().getString("messages.prefix") + "&7" + killer.getName() + "&f unlocked certified killer"));
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.0f);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GREEN + "Achievement Unlocked", ChatColor.WHITE + "Certified Killer"), 100L);
            }

            // TODO: Implement more, or make them configurable
        }
    }
}
