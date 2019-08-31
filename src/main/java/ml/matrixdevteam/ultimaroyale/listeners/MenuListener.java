package ml.matrixdevteam.ultimaroyale.listeners;

import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import ml.matrixdevteam.ultimaroyale.arena.Arenas;
import ml.matrixdevteam.ultimaroyale.util.UMaterial;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuListener implements Listener {
    static UltimaRoyale plugin;

    public MenuListener(final UltimaRoyale plugin) {
        MenuListener.plugin = plugin;
    }

    @EventHandler
    public void openInventories(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType() == UMaterial.CLAY_BALL.getMaterial() && player.getItemInHand().getItemMeta().getDisplayName().equals("&cAchievements")) {
            achievement(player);
            e.setCancelled(true);
        }

        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType() == UMaterial.EMERALD.getMaterial() && player.getItemInHand().getItemMeta().getDisplayName().equals("&aStats")) {
            statistics(player);
            e.setCancelled(true);
        }


    }

    public static void statistics(final Player player) {
        final String playerName = player.getName();
        final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userdata, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 5.0f, 1.0f);
        final Inventory inv = Bukkit.createInventory(null, 9, Arena.color(ChatColor.GREEN + playerName + "'s Statistics"));
        // TODO populate inventory
        player.openInventory(inv);
    }

    public static void achievement(final Player player) {
        final String playerName = player.getName();
        final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userdata, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 5.0f, 1.0f);
        final Inventory inv = Bukkit.createInventory(null, 18, Arena.color(ChatColor.GREEN + playerName + "'s Achievements"));

        if (playerData.getInt("deaths") >= 10) {
            final ItemStack dead10 = UMaterial.LIME_DYE.getItemStack();
            final ItemMeta dead10Meta = dead10.getItemMeta();
            dead10Meta.setDisplayName(Arena.color("&a&k%&r &aYou tried &a&k%&r &a[Unlocked]"));
            final List<String> dead10Lore = new ArrayList<>();
            dead10Lore.clear();
            dead10Lore.add(Arena.color("&e&lObjective: &r&7Get eliminated 10 times."));
            dead10Lore.add(Arena.color("&e&lReward: &r&6[+5 Points]"));
            dead10Meta.setLore(dead10Lore);
            dead10.setItemMeta(dead10Meta);
            inv.setItem(0, dead10);
        } else {
            final ItemStack dead10i = UMaterial.RED_DYE.getItemStack();
            final ItemMeta dead10iMeta = dead10i.getItemMeta();
            dead10iMeta.setDisplayName(Arena.color("&cYou tried &7[Locked]"));
            final List<String> dead10iLore = new ArrayList<>();
            dead10iLore.clear();
            dead10iLore.add(Arena.color("&c&lObjective: &r&7Get eliminated 10 times."));
            dead10iLore.add(Arena.color("&c&lReward: &r&6[+5 Points]"));
            dead10iMeta.setLore(dead10iLore);
            dead10i.setItemMeta(dead10iMeta);
            inv.setItem(0, dead10i);
        }

        // TODO Make more, or make them configurable.

        player.openInventory(inv);
    }

    public static void trailsShop(final Player player) {
        final String playerName = player.getName();
        final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userdata, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 5.0f, 1.0f);
        final Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Item Shop");

        // TODO populate inventory

        player.openInventory(inv);
    }

    @EventHandler
    public void trailsShopClick(final InventoryClickEvent e) {
        final Player player = (Player) e.getWhoClicked();
        final String playerName = player.getName();
        final ItemStack clicked = e.getCurrentItem();
        final Inventory inv = e.getInventory();

        if (!Arenas.isInArena(player)) {
            return;
        }

        final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userdata, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);

        if (playerData.getBoolean("lobby")) {
            e.setCancelled(true);
            if (e.getView().getTitle().equals(ChatColor.GREEN + "Item Shop")) {
                if (clicked == null || e.getCurrentItem().getType() == UMaterial.AIR.getMaterial()) {
                    e.setCancelled(true);
                } else if (clicked.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Green Trail")) {
                    e.setCancelled(true);
                    final EconomyResponse r = UltimaRoyale.eco.withdrawPlayer(player, 150);
                    if (r.transactionSuccess()) {
                        if (!f.exists()) {
                            try {
                                playerData.createSection("particles");
                                playerData.set("particles.green", true);
                                playerData.set("particles.red", false);
                                // TODO others
                                playerData.save(f);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                playerData.set("particles.green", true);
                                playerData.set("particles.red", false);
                                // TODO others
                                playerData.save(f);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                        player.closeInventory();
                        UltimaRoyale.sendMessage(player, "You've purchased 'Green Trails'");
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }
}
