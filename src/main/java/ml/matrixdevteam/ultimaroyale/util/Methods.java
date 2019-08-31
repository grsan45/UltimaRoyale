package ml.matrixdevteam.ultimaroyale.util;

import de.slikey.effectlib.effect.DonutEffect;
import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Methods {
    static UltimaRoyale plugin;

    public Methods(final UltimaRoyale plugin) {
        Methods.plugin = plugin;
    }

    public static UltimaRoyale getPlugin() {
        return plugin;
    }

    public static void setBTBInv(final Player player) {
        player.getInventory().clear();
        player.updateInventory();
        final ItemStack elytra = UMaterial.ELYTRA.getItemStack();
        final ItemMeta elytraMeta = elytra.getItemMeta();
        elytra.setDurability((short) 0);
        elytraMeta.setDisplayName(ChatColor.GREEN + "Glider");

        final ItemStack harvestingTool = UMaterial.WOODEN_PICKAXE.getItemStack();
        final ItemMeta harvestingToolMeta = harvestingTool.getItemMeta();
        harvestingToolMeta.setUnbreakable(true);
        harvestingToolMeta.setDisplayName(ChatColor.GREEN + "Harvesting Tool");
        harvestingTool.setItemMeta(harvestingToolMeta);
        player.getInventory().setItem(0, harvestingTool);

        final ItemStack glass = UMaterial.ORANGE_STAINED_GLASS.getItemStack();
        player.getInventory().setItem(35, glass);
        player.getInventory().setItem(34, glass);
        player.getInventory().setItem(33, glass);
        player.getInventory().setItem(32, glass);
        player.getInventory().setItem(31, glass);
        player.getInventory().setItem(30, glass);
        player.getInventory().setItem(29, glass);
        player.getInventory().setItem(28, glass);
        player.getInventory().setItem(26, glass);
        player.getInventory().setItem(25, glass);
        player.getInventory().setItem(24, glass);
        player.getInventory().setItem(23, glass);
        player.getInventory().setItem(22, glass);
        player.getInventory().setItem(21, glass);
        player.getInventory().setItem(20, glass);
        player.getInventory().setItem(19, glass);
        player.getInventory().setItem(17, glass);
        player.getInventory().setItem(16, glass);
        player.getInventory().setItem(15, glass);
        player.getInventory().setItem(14, glass);
        player.getInventory().setItem(13, glass);
        player.getInventory().setItem(12, glass);
        player.getInventory().setItem(11, glass);
        player.getInventory().setItem(10, glass);

        player.getPlayer().getInventory().setChestplate(elytra);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.setGameMode(GameMode.SURVIVAL);
            if (!player.isGliding()) {
                player.setGliding(true);
                player.getEyeLocation().setY(-40.0);
            }
        }, 5L);
        player.updateInventory();
    }

    public static void setSpecInventory(final Player player) {
        player.getInventory().clear();
        final ItemStack quitGame = UMaterial.RED_BED.getItemStack();
        final ItemMeta quitGameMeta = quitGame.getItemMeta();
        quitGameMeta.setDisplayName(ChatColor.RED + "Quit");
        quitGame.setItemMeta(quitGameMeta);
        player.getInventory().setItem(8, quitGame);
        player.updateInventory();
    }

    public static ItemStack createColorArmor(final ItemStack armor, final Color color) {
        final LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(color);
        armor.setItemMeta(meta);
        return armor;
    }

    public static List<Block> getNearbyCircleBlocks(final Location location, final int r, final int h, final boolean hollow,
                                                    final boolean sphere, final int plus_y) {
        final List<Block> circleBlocks = new ArrayList<>();
        final int cx = location.getBlockX();
        final int cy = location.getBlockY();
        final int cz = location.getBlockZ();

        for (int x = cx - r; x <= cx + r; ++x) {
            for (int z = cz - r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - r) : cy; y < (sphere ? (cy + r) : (cy + h)); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1) * (r - 1))) {
                        final Location l = new Location(location.getWorld(), x, y + plus_y, z);
                        circleBlocks.add(l.getBlock());
                    }
                }
            }
        }

        return circleBlocks;
    }

    public static List<Location> circle(final Location loc, final Integer r, final Integer h, final Boolean hollow, final Boolean sphere, final int plus_y) {
        final List<Location> circleblocks = new ArrayList<>();
        final int cx = loc.getBlockX();
        final int cy = loc.getBlockY();
        final int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; ++x) {
            for (int z = cz - r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - r) : cy; y < (sphere ? (cy + r) : (cy + h)); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1) * (r - 1))) {
                        final Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    public static List<Block> getNearbyBlocks(final Location location, final int Radius) {
        final List<Block> Blocks = new ArrayList<>();
        for (int X = location.getBlockX() - Radius; X <= location.getBlockX() + Radius; ++X) {
            for (int Y = location.getBlockY() - Radius; Y <= location.getBlockY() + Radius; ++Y) {
                for (int Z = location.getBlockZ() - Radius; Z <= location.getBlockZ() + Radius; ++Z) {
                    final Block block = location.getWorld().getBlockAt(X, Y, Z);
                    if (!block.isEmpty()) {
                        Blocks.add(block);
                    }
                }
            }
        }
        return Blocks;
    }

    public static Location getLobby() {
        if (plugin.arenas.contains("LobbySpawn.World")) {
            final Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("LobbySpawn.World")), plugin.arenas.getDouble("LobbySpawn.X"), plugin.arenas.getDouble("LobbySpawn.Y"), plugin.arenas.getDouble("LobbySpawn.Z"));
            loc.setPitch((float) plugin.arenas.getDouble("LobbySpawn.Pitch"));
            loc.setYaw((float) plugin.arenas.getDouble("LobbySpawn.Yaw"));
            return loc;
        }
        return null;
    }

    public static void setLobby(final Location loc) {
        if (!plugin.arenas.contains("LobbySpawn")) {
            plugin.arenas.addDefault("LobbySpawn.X", loc.getX());
            plugin.arenas.addDefault("LobbySpawn.Y", loc.getY());
            plugin.arenas.addDefault("LobbySpawn.Z", loc.getZ());
            plugin.arenas.addDefault("LobbySpawn.World", loc.getWorld().getName());
            plugin.arenas.addDefault("LobbySpawn.Pitch", loc.getPitch());
            plugin.arenas.addDefault("LobbySpawn.Yaw", loc.getYaw());
        } else {
            plugin.arenas.set("LobbySpawn.X", loc.getX());
            plugin.arenas.set("LobbySpawn.Y", loc.getY());
            plugin.arenas.set("LobbySpawn.Z", loc.getZ());
            plugin.arenas.set("LobbySpawn.World", loc.getWorld().getName());
            plugin.arenas.set("LobbySpawn.Pitch", loc.getPitch());
            plugin.arenas.set("LobbySpawn.Yaw", loc.getYaw());
        }

        saveYamls();
    }

    public static void addToList(final Arena arena) {
        if (plugin.arenas.contains("Arenas.List")) {
            final List<String> list = plugin.arenas.getStringList("Arenas.List");
            list.add(arena.getName());
            plugin.arenas.set("Arenas.List", list);
        } else {
            final List<String> list = new ArrayList<>();
            list.add(arena.getName());
            plugin.arenas.addDefault("Arenas.List", list);
        }
    }

    public static void removeFromList(final String name) {
        if (plugin.arenas.contains("Arenas.List")) {
            final List<String> list = plugin.arenas.getStringList("Arenas.List");
            list.remove(name);
            plugin.arenas.set("Arenas.List", list);
        }
    }

    public static void saveYamls() {
        try {
            plugin.arenas.save(plugin.arenasFile);
        } catch (IOException ignored) {
        }
    }

    public static void loadYamls() {
        try {
            plugin.arenas.load(plugin.arenasFile);
        } catch (Exception ignored) {
        }
    }

    public static void setLobbyInventory(final Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        final ItemStack gameQuit = UMaterial.RED_BED.getItemStack();
        final ItemMeta gameQuitMeta = gameQuit.getItemMeta();
        gameQuitMeta.setDisplayName(ChatColor.RED + "Quit");
        gameQuit.setItemMeta(gameQuitMeta);
        player.getInventory().setItem(8, gameQuit);

        final ItemStack achievements = UMaterial.CLAY_BALL.getItemStack();
        final ItemMeta achievementMeta = achievements.getItemMeta();
        achievementMeta.setDisplayName(ChatColor.RED + "Achievements");
        achievements.setItemMeta(achievementMeta);
        player.getInventory().setItem(0, achievements);

        final ItemStack shop = UMaterial.NETHER_STAR.getItemStack();
        final ItemMeta shopMeta = shop.getItemMeta();
        shopMeta.setDisplayName(ChatColor.RED + "Shop");
        shop.setItemMeta(shopMeta);
        player.getInventory().setItem(1, shop);

        final ItemStack stats = UMaterial.EMERALD.getItemStack();
        final ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(ChatColor.GREEN + "Stats");
        stats.setItemMeta(statsMeta);
        player.getInventory().setItem(4, stats);
        player.updateInventory();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            player.setGameMode(GameMode.ADVENTURE);
            final DonutEffect vortex = new DonutEffect(UltimaRoyale.effectManager);
            vortex.setLocation(player.getLocation());
            vortex.iterations = 25;
            vortex.radiusDonut = 0.5f;
            vortex.radiusTube = 0.5f;
            vortex.yRotation = 90.0;
            vortex.speed = 50.0f;
            vortex.duration = 20;
            vortex.particle = Particle.SPELL_WITCH;
            vortex.start();
        }, 10L);
    }

    public void firstRun() throws Exception {
        if (!plugin.arenasFile.exists()) {
            plugin.arenasFile.getParentFile().mkdirs();
            this.copy(plugin.getResource("arenas.yml"), plugin.arenasFile);
        }
    }

    private void copy(final InputStream in, final File file) {
        try {
            final OutputStream out = new FileOutputStream(file);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception ignored) {
        }
    }
}
