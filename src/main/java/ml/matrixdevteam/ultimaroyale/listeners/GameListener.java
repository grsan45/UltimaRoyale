package ml.matrixdevteam.ultimaroyale.listeners;

import de.slikey.effectlib.effect.ShieldEffect;
import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import ml.matrixdevteam.ultimaroyale.arena.Arenas;
import ml.matrixdevteam.ultimaroyale.arena.LeaveReason;
import ml.matrixdevteam.ultimaroyale.util.FileManager;
import ml.matrixdevteam.ultimaroyale.util.Methods;
import ml.matrixdevteam.ultimaroyale.util.UMaterial;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GameListener implements Listener {
    public static FileManager settings = FileManager.getInstance();
    UltimaRoyale plugin;

    public GameListener(final UltimaRoyale plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);
        if (arena.isOnline()) {
            final Location loc = arena.getRandomSpawn();
            if (loc != null) {
                e.setRespawnLocation(loc);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                player.setHealth(20.0);
            }
        }
    }

    @EventHandler
    public void onClickQuit(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getPlayer().getItemInHand().getType() != UMaterial.RED_BED.getMaterial()) {
                return;
            }

            arena.removePlayer(player, LeaveReason.QUIT);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        final String playerName = player.getName();
        final File userData = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userData, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline()) {
            if (playerData.getBoolean("dead")) {
                e.setCancelled(true);
            } else if (!playerData.getBoolean("dead")) {
                e.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void openAchievementInv(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (e.getPlayer().getItemInHand().getType() == UMaterial.MELON_SLICE.getMaterial()) {
                e.setCancelled(true);
                player.setItemInHand(null);
                player.updateInventory();
                player.sendTitle("5", "");

                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GRAY + "4", ""), 20L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GRAY + "3", ""), 40L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GRAY + "2", ""), 60L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> player.sendTitle(ChatColor.GRAY + "1", ""), 80L);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.setHealth(20.0);
                    player.sendTitle("", ChatColor.RED + "Health Full");
                }, 100L);
            }

            if (e.getPlayer().getItemInHand().getType() == UMaterial.GOLD_INGOT.getMaterial()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 1));
                e.setCancelled(true);
                player.setItemInHand(null);
                player.updateInventory();
            }
        }
    }

    public void breakTree(final Block tree) {
        if (tree.getType() != UMaterial.match("LOG").getMaterial() && tree.getType() != UMaterial.match("LEAVES").getMaterial()) {
            return;
        }

        tree.breakNaturally();
        BlockFace[] values;

        for (int length = (values = BlockFace.values()).length, i = 0; i < length; ++i) {
            final BlockFace face = values[i];
            breakTree(tree.getRelative(face));
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline()) {
            if (e.getBlock().getType() == UMaterial.match("LOG").getMaterial()) {
                breakTree(e.getBlock());
                for (final ItemStack item : e.getBlock().getDrops()) {
                    e.getPlayer().getInventory().setItem(27, item);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(final EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player player = (Player) e.getEntity();
            final ItemStack item = e.getItem().getItemStack();
            if (item.getType() == UMaterial.match("LOG").getMaterial()) {
                player.getInventory().setItem(9, item);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onOpenChest(final PlayerInteractEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);

        if (arena.isOnline() && e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getType() == UMaterial.CHEST.getMaterial()) {
            e.setUseInteractedBlock(Result.DENY);
            final World world = player.getWorld();
            final ArrayList<ItemStack> drops = new ArrayList<>();
            final ItemStack potion = UMaterial.MELON_SLICE.getItemStack();
            final ItemMeta potionMeta = potion.getItemMeta();
            potionMeta.setDisplayName(ChatColor.GREEN + "Medkit");
            potion.setItemMeta(potionMeta);
            drops.add(potion);

            final ItemStack invis = UMaterial.GOLD_INGOT.getItemStack();
            final ItemMeta invisMeta = invis.getItemMeta();
            invisMeta.setDisplayName(ChatColor.GRAY + "Invisibility");
            invis.setItemMeta(invisMeta);
            drops.add(invis);

            drops.add(UMaterial.DIAMOND.getItemStack());
            drops.add(UMaterial.IRON_SWORD.getItemStack());
            drops.add(UMaterial.SHIELD.getItemStack());
            drops.add(UMaterial.SPLASH_POTION_HEALING_1.getItemStack());
            drops.add(UMaterial.IRON_HELMET.getItemStack());
            drops.add(UMaterial.DIAMOND_AXE.getItemStack());
            drops.add(UMaterial.EGG.getItemStack());
            drops.add(UMaterial.FIREWORK_ROCKET.getItemStack());
            drops.add(UMaterial.APPLE.getItemStack());
            drops.add(UMaterial.GOLDEN_LEGGINGS.getItemStack());
            drops.add(new ItemStack(UMaterial.ARROW.getMaterial(), 20));
            drops.add(UMaterial.IRON_BOOTS.getItemStack());
            drops.add(UMaterial.SPIDER_EYE.getItemStack());
            drops.add(UMaterial.BOW.getItemStack());

            final int size = drops.size();
            final int random = ThreadLocalRandom.current().nextInt(size);
            final ItemStack picked = drops.get(random);
            world.dropItemNaturally(e.getClickedBlock().getLocation(), picked);

            final int size2 = drops.size();
            final int random2 = ThreadLocalRandom.current().nextInt(size2);
            final ItemStack picked2 = drops.get(random2);
            world.dropItemNaturally(e.getClickedBlock().getLocation(), picked2);

            final int size3 = drops.size();
            final int random3 = ThreadLocalRandom.current().nextInt(size3);
            final ItemStack picked3 = drops.get(random3);
            world.dropItemNaturally(e.getClickedBlock().getLocation(), picked3);

            e.getClickedBlock().setType(UMaterial.AIR.getMaterial());
            final Location loc = new Location(e.getClickedBlock().getWorld(), e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ());
            final ShieldEffect shield = new ShieldEffect(UltimaRoyale.effectManager);
            shield.setLocation(loc);
            shield.iterations = 9;
            shield.particle = Particle.REDSTONE;
            shield.color = Color.RED;
            shield.radius = 1;
            shield.start();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        final Player killer = e.getEntity().getKiller();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);
        if (arena.isOnline()) {
            if (killer != null) {
                final File userdataArena = new File(plugin.getDataFolder(), File.separator + "arenas");
                final File arenaf = new File(userdataArena, File.separator + "ArenasState.yml");
                final FileConfiguration playerDataArena = YamlConfiguration.loadConfiguration(arenaf);
                if (arenaf.exists()) {
                    try {
                        playerDataArena.createSection(arena.getName());
                        playerDataArena.set(arena.getName() + ".AlivePlayers", arena.getPlayers().size() - 1);
                        playerDataArena.save(arenaf);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                final Scoreboard board = player.getScoreboard();
                final Score alive = board.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GRAY + "Alive: ");
                alive.setScore(playerDataArena.getInt(arena.getName() + ".AlivePlayers"));
                e.getDrops().clear();
                e.setDeathMessage("");
                e.setDroppedExp(0);
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                arena.sendAll(Arena.color(settings.getConfig().getString("Messages.Dead")));
                for (final Player ps : Bukkit.getServer().getOnlinePlayers()) {
                    ps.hidePlayer(player);
                }
                player.getInventory().clear();
                player.updateInventory();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SHULKER_DEATH, 15.0f, 1.0f);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                player.setHealth(20.0);
                player.setFireTicks(0);
                final String deadPlayerName = player.getName();
                final File deadUserData = new File(plugin.getDataFolder(), File.separator + "players");
                final File deadf = new File(deadUserData, File.separator + deadPlayerName + ".yml");
                final FileConfiguration deadPlayerData = YamlConfiguration.loadConfiguration(deadf);
                if (deadf.exists()) {
                    try {
                        final int i = deadPlayerData.getInt("deaths");
                        final int i2 = deadPlayerData.getInt(arena.getName() + ".deaths");
                        deadPlayerData.set("deaths", 1 + i);
                        deadPlayerData.set(arena.getName() + ".deaths", 1 + i2);
                        deadPlayerData.save(deadf);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                final String killerPlayerName = killer.getName();
                final File killerUserData = new File(plugin.getDataFolder(), File.separator + "players");
                final File killerf = new File(killerUserData, File.separator + killerPlayerName + ".yml");
                final FileConfiguration killerPlayerData = YamlConfiguration.loadConfiguration(killerf);
                if (killerf.exists()) {
                    try {
                        final int j = killerPlayerData.getInt("elims");
                        final int i3 = killerPlayerData.getInt(arena.getName() + ".elims");
                        killerPlayerData.set("elims", 1 + j);
                        killerPlayerData.set(arena.getName() + ".elims", 1 + i3);
                        killerPlayerData.save(killerf);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                final Score elims = board.getObjective(DisplaySlot.SIDEBAR).getScore(ChatColor.GRAY + "Eliminations: ");
                elims.setScore(killerPlayerData.getInt(arena.getName() + ".elims"));
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    if (!Arenas.isInArena(player)) {
                        return;
                    }

                    if (arena.isOnline()) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(killer);
                        player.setSpectatorTarget(killer);
                        player.setHealth(20.0);
                        final int left = playerDataArena.getInt(arena.getName() + ".AlivePlayers");
                        player.sendTitle(ChatColor.YELLOW + "TOP " + (left + 1), "");
                        player.setFireTicks(0);
                        Methods.setSpecInventory(player);
                    }
                }, 10L);

                if (playerDataArena.getInt(arena.getName() + ".AlivePlayers") <= 1) {
                    if (killerf.exists()) {
                        try {
                            final int k = killerPlayerData.getInt("wins");
                            killerPlayerData.set("wins", 1 + k);
                            killerPlayerData.save(killerf);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.BLUE + "=================" + ChatColor.GRAY + ">> " + ChatColor.GOLD + "UltimaRoyale" + ChatColor.GRAY + " <<" + ChatColor.BLUE + "=================");
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("               " + ChatColor.RED + killer.getName() + ChatColor.GRAY + " won! In arena " + ChatColor.BLUE + arena.getName());
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.BLUE + "=================" + ChatColor.GRAY + ">> " + ChatColor.GOLD + "UltimaRoyale" + ChatColor.GRAY + " <<" + ChatColor.BLUE + "=================");

                    killer.setAllowFlight(false);
                    killer.sendTitle(ChatColor.YELLOW + "Victory #1", "");

                    for (final Player ps : Bukkit.getServer().getOnlinePlayers()) {
                        killer.showPlayer(ps);
                    }

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK), 60L);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK), 80L);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK), 100L);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK), 200L);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        killer.getWorld().spawnEntity(killer.getLocation(), EntityType.FIREWORK);
                        arena.stop();
                    }, 250L);
                }
            } else {
                e.getDrops().clear();
                e.setDeathMessage("");
                e.setDroppedExp(0);
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final String playerName = player.getName();
        final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
        final File f = new File(userdata, File.separator + playerName + ".yml");
        final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
        if (!f.exists()) {
            try {
                playerData.set("wins", 0);
                playerData.set("deaths", 0);
                playerData.set("elims", 0);
                playerData.set("dead", false);
                playerData.set("lobby", false);
                playerData.set("ingame", false);
                playerData.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        if (!Arenas.isInArena(player)) {
            return;
        }

        final Arena arena = Arenas.getArena(player);
        player.teleport(Methods.getLobby());
        player.getInventory().clear();
        arena.removePlayer(player, LeaveReason.QUIT);
    }
}
