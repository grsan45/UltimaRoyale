package ml.matrixdevteam.ultimaroyale.arena;

import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import ml.matrixdevteam.ultimaroyale.util.FileManager;
import ml.matrixdevteam.ultimaroyale.util.Methods;
import ml.matrixdevteam.ultimaroyale.util.UMaterial;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Arena {
    public static FileManager settings = FileManager.getInstance();
    static ArrayList<String> a = new ArrayList<>();
    final ScoreboardManager manager;
    final Scoreboard board;
    UltimaRoyale plugin;
    Team team;
    private String name;
    private GameState state;
    private int id, counter, endTime, timecheckId, spawnNum;
    private boolean endtimeOn;
    private List<UUID> players;
    private Scoreboard scoreboard;
    private HashMap<UUID, ItemStack[]> armor, inventory;

    public Arena(final String name) {
        this.state = GameState.LOBBY;
        this.id = 0;
        this.endtimeOn = false;
        players = new ArrayList<>();
        this.timecheckId = 0;
        this.armor = new HashMap<>();
        this.inventory = new HashMap<>();
        this.spawnNum = 8;
        this.manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();
        this.team = null;
        this.name = name;
        this.plugin = Methods.getPlugin();
    }

    public static String color(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static void access$2(final Arena arena, final int counter) {
        arena.counter = counter;
    }

    private void saveInventory(final Player player) {
        armor.put(player.getUniqueId(), player.getInventory().getArmorContents());
        inventory.put(player.getUniqueId(), player.getInventory().getContents());
        player.getInventory().setArmorContents(null);
        player.getInventory().clear();
        player.updateInventory();
    }

    private void loadInventory(final Player player) {
        if (armor.containsKey(player.getUniqueId())) {
            player.getInventory().setArmorContents(armor.get(player.getUniqueId()));
            armor.remove(player.getUniqueId());
        }

        if (inventory.containsKey(player.getUniqueId())) {
            player.getInventory().setContents(inventory.get(player.getUniqueId()));
            inventory.remove(player.getUniqueId());
        }

        player.updateInventory();
    }

    public void sendAll(final String message) {
        final List<UUID> nulls = new ArrayList<>();
        for (final UUID ids : players) {
            if (Bukkit.getPlayer(ids) != null) {
                UltimaRoyale.sendMessage(Bukkit.getPlayer(ids), message);
            } else {
                nulls.add(ids);
            }
        }

        for (final UUID s : nulls) {
            players.remove(s);
        }

        nulls.clear();
    }

    public void playSoundAll(final String name, final Integer volume, final Integer pitch) {
        final List<UUID> nulls = new ArrayList<>();
        for (final UUID id : players) {
            if (Bukkit.getPlayer(id) != null) {
                Bukkit.getPlayer(id).playSound(Bukkit.getPlayer(id).getLocation(), name, volume, pitch);
            } else {
                nulls.add(id);
            }
        }

        for (final UUID s : nulls) {
            players.remove(s);
        }

        nulls.clear();
    }

    public GameState getState() {
        return state;
    }

    public void setState(final GameState state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Location getRandomSpawn() {
        final Random random = ThreadLocalRandom.current();
        if (plugin.arenas.contains("Arenas." + getName() + ".Spawns.Counter")) {
            final int other = plugin.arenas.getInt("Arenas." + getName() + ".Spawns.Counter");
            final Integer num = random.nextInt(other);
            final Location loc = getSpawn(num);
            return loc;
        }

        return null;
    }

    public Location getRandomChest() {
        final Random rand = ThreadLocalRandom.current();
        if (plugin.arenas.contains("Arenas." + getName() + ".Chests.Counter")) {
            final int other = plugin.arenas.getInt("Arenas." + getName() + ".Chests.Counter");
            final int num = rand.nextInt(other);
            final Location loc = getSpawn(num);
            return loc;
        }

        return null;
    }

    public Location getNextSpawn() {
        if (plugin.arenas.contains("Arenas." + getName() + ".Spawns.Counter")) {
            final Location loc = getSpawn(spawnNum);
            final int totalSpawnCount = plugin.arenas.getInt("Arenas." + getName() + ".Spawns.Counter");
            ++spawnNum;

            if (spawnNum >= totalSpawnCount) {
                spawnNum = 0;
            }

            return loc;
        }

        return null;
    }

    public Location getSpawn(final int id) {
        if (plugin.arenas.contains("Arenas." + getName() + ".Spawns." + id + ".World")) {
            final Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("Arenas." + getName() + ".Spawns." + id + ".World")), plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".X"), plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Y"), plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Z"));
            loc.setPitch((float) plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Pitch"));
            loc.setYaw((float) plugin.arenas.getDouble("Arenas." + getName() + ".Spawns." + id + ".Yaw"));
            return loc;
        }

        return null;
    }

    public Location getSpawnChests(final int id) {
        if (plugin.arenas.contains("Arenas." + getName() + ".Chests." + id + ".World")) {
            final Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("Arenas." + getName() + ".Chests." + id + ".World")), plugin.arenas.getDouble("Arenas." + getName() + ".Chests." + id + ".X"), plugin.arenas.getDouble("Arenas." + getName() + ".Chests." + id + ".Y"), plugin.arenas.getDouble("Arenas." + getName() + ".Chests." + id + ".Z"));
            loc.setPitch((float) plugin.arenas.getDouble("Arenas." + this.getName() + ".Chests." + id + ".Pitch"));
            loc.setYaw((float) plugin.arenas.getDouble("Arenas." + this.getName() + ".Chests." + id + ".Yaw"));
            return loc;
        }

        return null;
    }

    public void addSpawn(final Location loc) {
        if (!plugin.arenas.contains("Arenas." + getName() + ".Spawns.1.X")) {
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.Counter", 2);
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".X", loc.getX());
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Y", loc.getY());
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Z", loc.getZ());
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".World", loc.getWorld().getName());
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Pitch", loc.getPitch());
            plugin.arenas.addDefault("Arenas." + getName() + ".Spawns.1" + ".Yaw", loc.getYaw());
        } else {
            int counter = plugin.arenas.getInt("Arenas." + getName() + ".Spawns.Counter");
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".X", loc.getX());
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Y", loc.getY());
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Z", loc.getZ());
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".World", loc.getWorld().getName());
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Pitch", loc.getPitch());
            plugin.arenas.set("Arenas." + getName() + ".Spawns." + counter + ".Yaw", loc.getYaw());
            ++counter;
            plugin.arenas.set("Arenas." + getName() + ".Spawns.Counter", counter);
        }

        Methods.saveYamls();
    }

    public void addChest(final Location loc) {
        if (!plugin.arenas.contains("Arenas." + getName() + ".Chests.1.X")) {
            plugin.arenas.addDefault("Arenas." + getName() + ".ChestsCounter", 2);
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".X", loc.getX());
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".Y", loc.getY());
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".Z", loc.getZ());
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".World", loc.getWorld().getName());
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".Pitch", loc.getPitch());
            plugin.arenas.addDefault("Arenas." + getName() + ".Chests.1" + ".Yaw", loc.getYaw());
        } else {
            int counter = plugin.arenas.getInt("Arenas." + getName() + ".ChestsCounter");
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".X", loc.getX());
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".Y", loc.getY());
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".Z", loc.getZ());
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".World", loc.getWorld().getName());
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".Pitch", loc.getPitch());
            plugin.arenas.set("Arenas." + getName() + ".Chests." + counter + ".Yaw", loc.getYaw());
            ++counter;
            plugin.arenas.set("Arenas." + getName() + ".ChestsCounter", counter);
        }

        Methods.saveYamls();
    }

    private void timeCheck() {
        timecheckId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (getState() == GameState.IN_GAME) {
                check();
            } else {
                Bukkit.getScheduler().cancelTask(timecheckId);
            }
        }, 400L, 20L);
    }

    public void check() {
    }

    public Location getLobbySpawn() {
        if (plugin.arenas.contains("Arenas." + getName() + ".Lobby.Spawn.World")) {
            final Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("Arenas." + getName() + ".Lobby.Spawn" + ".World")), plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".X"), plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Y"), plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Z"));
            loc.setPitch((float) plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch"));
            loc.setYaw((float) plugin.arenas.getDouble("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw"));
            return loc;
        }

        return null;
    }

    public void setLobbySpawn(final Location loc) {
        if (!plugin.arenas.contains("Arenas." + getName() + ".Lobby.Spawn")) {
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".X", loc.getX());
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Y", loc.getY());
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Z", loc.getZ());
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".World", loc.getWorld().getName());
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch", loc.getPitch());
            plugin.arenas.addDefault("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw", loc.getYaw());
        } else {
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".X", loc.getX());
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Y", loc.getY());
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Z", loc.getZ());
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".World", loc.getWorld().getName());
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Pitch", loc.getPitch());
            plugin.arenas.set("Arenas." + getName() + ".Lobby.Spawn" + ".Yaw", loc.getYaw());
        }

        Methods.saveYamls();
    }

    public boolean isOnline() {
        return getState() == GameState.IN_GAME || getState() == GameState.STOPPING;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void healAll() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                Bukkit.getPlayer(s).setHealth(20.0);
                Bukkit.getPlayer(s).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                Bukkit.getPlayer(s).setFoodLevel(20);
            }
        }
    }

    public void liveAll() {
        for (final UUID s : players) {
            for (final Arena arena : Arenas.getArenas().values()) {
                if (Bukkit.getPlayer(s) != null) {
                    final File userdata = new File(plugin.getDataFolder(), File.separator + "arenas");
                    final File f = new File(userdata, File.separator + "ArenasState.yml");
                    final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                    if (!f.exists()) {
                        try {
                            playerData.createSection(getName());
                            playerData.set(getName() + ".AlivePlayers", arena.getPlayers().size());
                            playerData.save(f);
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                    if (!f.exists()) {
                        continue;
                    }
                    try {
                        playerData.createSection(getName());
                        playerData.set(getName() + ".AlivePlayers", arena.getPlayers().size());
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private void setInventories() {
        for (final UUID s : getPlayers()) {
            if (Bukkit.getPlayer(s) != null) {
                final String player = Bukkit.getPlayer(s).getName();
                final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
                final File f = new File(userdata, File.separator + player + ".yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                if (f.exists()) {
                    try {
                        playerData.set("lobby", false);
                        playerData.save(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Methods.setBTBInv(Bukkit.getPlayer(s));
            }
        }
    }

    private void setScoreboard() {
        final ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();
        final Objective main = board.registerNewObjective(ChatColor.GREEN + "Game", "Kills");
        main.setDisplaySlot(DisplaySlot.SIDEBAR);

        for (final UUID s : getPlayers()) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                final File userdata = new File(plugin.getDataFolder(), File.separator + "arenas");
                final File f = new File(userdata, File.separator + "ArenasState.yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                final Score alive = main.getScore(ChatColor.GRAY + "Alive: ");
                final Score elims = main.getScore(ChatColor.GRAY + "Eliminations: ");
                alive.setScore(playerData.getInt(getName() + ".AlivePlayers"));

                final String killerPlayerName = player.getName();
                final File killerUserData = new File(plugin.getDataFolder(), File.separator + "players");
                final File killerf = new File(killerUserData, File.separator + killerPlayerName + ".yml");
                final FileConfiguration killerPlayerData = YamlConfiguration.loadConfiguration(killerf);
                elims.setScore(killerPlayerData.getInt(getName() + ".kills"));

                try {
                    killerPlayerData.set(getName() + ".kills", 0);
                    killerPlayerData.save(killerf);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                a.clear();
                a.add("1");

                final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

                scheduler.scheduleSyncRepeatingTask(plugin, () -> {
                    final int i = a.size();
                    if (i == 1) {
                        main.setDisplayName(color("&e&lUltimaRoyale"));
                        a.add("A");
                    }
                    if (i == 2) {
                        main.setDisplayName(color("&e&lUltimaRoyale"));
                        a.add("B");
                    }
                    if (i == 3) {
                        main.setDisplayName(color("&e&lUltimaRoyale"));
                        a.add("C");
                    }
                    if (i == 3) {
                        a.clear();
                        a.add("1");
                    }
                }, 0L, 5L);
                player.setScoreboard(board);
            }

            this.scoreboard = board;
        }
    }

    void spawnPlayers() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                final Location loc = getRandomSpawn();
                final String playerName = player.getName();
                final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
                final File f = new File(userdata, File.separator + playerName + ".yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                if (f.exists()) {
                    try {
                        playerData.set("dead", false);
                        playerData.set("lobby", false);
                        playerData.set("ingame", true);
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                if (loc != null) {
                    player.teleport(loc);
                    player.updateInventory();
                } else {
                    player.teleport(getSpawn(1));
                    player.updateInventory();
                }
            }
        }
    }

    void spawnChests() {
        for (final String chests : plugin.arenas.getConfigurationSection("Arenas." + this.getName() + ".Chests").getKeys(false)) {
            getSpawnChests(Integer.parseInt(chests)).getBlock().setType(UMaterial.CHEST.getMaterial());
        }
    }

    void playsound() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
            }
        }
    }

    void title5() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle("", ChatColor.DARK_RED + "5");
            }
        }
    }

    void title4() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle("", ChatColor.RED + "4");
            }
        }
    }

    void title3() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle(ChatColor.GOLD + "3", "");
            }
        }
    }

    void title2() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle(ChatColor.YELLOW + "2", "");
            }
        }
    }

    void title1() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle(ChatColor.GREEN + "1", "");
            }
        }
    }

    void gliding() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player p = Bukkit.getPlayer(s);
                p.getVelocity().multiply(0.2);
            }
        }
    }

    void title0() {
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                final String playerName = player.getName();
                final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
                final File f = new File(userdata, File.separator + playerName + ".yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                if (!f.exists()) {
                    try {
                        playerData.set("dead", false);
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                if (f.exists()) {
                    try {
                        playerData.set("dead", false);
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10.0f, 1.0f);
                player.sendTitle(ChatColor.GREEN + "GO", "");
                player.setAllowFlight(false);
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    public void start() {
        if (getState() == GameState.IN_GAME || getState() == GameState.STARTING || getState() == GameState.STOPPING) {
            return;
        }
        this.counter = plugin.getConfig().getInt(this.getName() + ".Countdown");
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (counter > 0) {
                setState(GameState.STARTING);
                updateSigns();
                if (counter == 60) {
                    sendAll(color(settings.getConfig().getString("Messages.startInOneMin")));
                    playsound();
                }
                if (counter == 30) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                }
                if (counter == 45) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                }
                if (counter == 15) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                }
                if (counter == 10) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                }
                if (counter == 5) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                    title5();
                }
                if (counter == 4) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                    title4();
                }
                if (counter == 3) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                    title3();
                }
                if (counter == 2) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                    title2();
                }
                if (counter == 1) {
                    sendAll(counter + color(settings.getConfig().getString("Messages.start")));
                    playsound();
                    title1();
                }
                final Arena this$0 = Arena.this;
                Arena.access$2(this$0, this$0.counter - 1);
            } else {
                sendAll(Arena.settings.getConfig().getString("Messages.Started").replaceAll("&", "§"));
                setState(GameState.IN_GAME);
                startGameTimer();
                healAll();
                liveAll();
                setScoreboard();
                Bukkit.getScheduler().cancelTask(id);
                updateSigns();
                updateSigns();
                setInventories();
                sendAll(Arena.settings.getConfig().getString("Messages.WinGoal").replaceAll("&", "§"));
                spawnPlayers();
                spawnChests();
                title0();
                gliding();
            }
        }, 0L, 20L);
    }

    public void sendAll1(final String Message, final String Message1) {
        final List<UUID> nulls = new ArrayList<UUID>();
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                Bukkit.getPlayer(s).sendTitle(Message, Message1);
            } else {
                nulls.add(s);
            }
        }
        for (final UUID s : nulls) {
            players.remove(s);
        }
        nulls.clear();
    }

    public Location getChestSpawn() {
        final File userdataArena = new File(plugin.getDataFolder(), File.separator + "arenas");
        final File fArena = new File(userdataArena, File.separator + "ArenasState.yml");
        final FileConfiguration playerDataArena = YamlConfiguration.loadConfiguration(fArena);
        if (fArena.exists()) {
            final Iterator<String> iterator = playerDataArena.getConfigurationSection(this.getName() + ".ChestLoc").getKeys(false).iterator();
            if (iterator.hasNext()) {
                final String Chests = iterator.next();
                final Location loc = new Location(Bukkit.getWorld(playerDataArena.getString(Chests + ".world")), playerDataArena.getInt(Chests + ".x"), playerDataArena.getInt(Chests + ".y"), playerDataArena.getInt(Chests + ".z"));
                for (final UUID s : players) {
                    if (Bukkit.getPlayer(s) != null) {
                        final Player player = Bukkit.getPlayer(s);
                        player.sendMessage(loc.toString());
                    }
                }
                return loc;
            }
        }
        return null;
    }

    public void stop() {
        if (getState() == GameState.STARTING) {
            Bukkit.getScheduler().cancelTask(id);
        }
        this.setState(GameState.STOPPING);
        this.updateSigns();
        this.healAll();
        if (this.endtimeOn) {
            Bukkit.getScheduler().cancelTask(endTime);
        }
        for (final UUID s : players) {
            if (Bukkit.getPlayer(s) != null) {
                final Player player = Bukkit.getPlayer(s);
                for (final Entity entity : player.getWorld().getEntities()) {
                    if (entity instanceof Item) {
                        entity.remove();
                    }
                }
                player.setGameMode(GameMode.ADVENTURE);
                if (Methods.getLobby() != null) {
                    player.teleport(Methods.getLobby());
                } else {
                    player.sendMessage(ChatColor.RED + "Main lobby does not exist.");
                }
                player.sendTitle("", ChatColor.GREEN + "Thanks for playing");
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                this.loadInventory(player);
                player.teleport(Methods.getLobby());
                player.setMaxHealth(20.0);
                player.setHealth(20.0);
                player.setFlying(false);
                player.setAllowFlight(false);
                this.loadInventory(player);
                player.setWalkSpeed(0.2f);
                player.setFlySpeed(0.1f);
                player.updateInventory();
                for (final PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                final String playerName = player.getName();
                final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
                final File f = new File(userdata, File.separator + playerName + ".yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                if (f.exists()) {
                    try {
                        playerData.set("dead", false);
                        playerData.set("lobby", false);
                        playerData.set("ingame", false);
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                for (final Player ps : Bukkit.getServer().getOnlinePlayers()) {
                    ps.showPlayer(player);
                }
                final File userdataArena = new File(plugin.getDataFolder(), File.separator + "arenas");
                final File fArena = new File(userdataArena, File.separator + "ArenasState.yml");
                final FileConfiguration playerDataArena = YamlConfiguration.loadConfiguration(fArena);
                if (fArena.exists()) {
                    try {
                        playerDataArena.set(this.getName() + ".Chest.Location", null);
                        playerDataArena.save(fArena);
                    } catch (IOException exception2) {
                        exception2.printStackTrace();
                    }
                }
                Arenas.removeArena(player);
            }
        }
        players.clear();
        this.endtimeOn = false;
        this.setState(GameState.LOBBY);
        this.updateSigns();
    }

    public void startGameTimer() {
        this.endtimeOn = true;
        this.endTime = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
        }, plugin.getConfig().getInt(getName() + ".EndTime") * 20);
    }

    public void updateSigns() {
        for (final Location loc : getSigns()) {
            if (loc.getBlock().getState() instanceof Sign) {
                final Sign sign = (Sign) loc.getBlock().getState();
                final int total = getPlayers().size();
                if (getState() == GameState.IN_GAME) {
                    sign.setLine(3, String.valueOf(ChatColor.BOLD) + total + "/" + getMaxPlayers());
                } else {
                    sign.setLine(3, String.valueOf(ChatColor.BOLD) + getPlayers().size() + "/" + getMaxPlayers());
                }
                if (getState() == GameState.IN_GAME) {
                    sign.setLine(2, ChatColor.RED + "IN GAME");
                } else if (getState() == GameState.LOBBY) {
                    sign.setLine(2, ChatColor.GREEN + "WAITING");
                } else if (getState() == GameState.STOPPING) {
                    sign.setLine(2, ChatColor.DARK_RED + "STOPPED");
                } else if (getState() == GameState.STARTING) {
                    sign.setLine(2, ChatColor.GOLD + "STARTING");
                }
                sign.update();
            }
        }
    }

    public List<Location> getSigns() {
        final String ArenaName = this.getName();
        final List<Location> locs = new ArrayList<>();
        for (int count = 1; plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + count + ".X"); ++count) {
            final Location loc = new Location(Bukkit.getWorld(plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + count + ".World")), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".X"), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Y"), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Z"));
            locs.add(loc);
        }
        return locs;
    }

    public void addSign(final Location loc) {
        final String Arena = getName();
        int counter = plugin.arenas.getInt("Arenas." + Arena + ".Signs.Counter");
        ++counter;
        plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".X", loc.getX());
        plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".Y", loc.getY());
        plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".Z", loc.getZ());
        plugin.arenas.addDefault("Arenas." + Arena + ".Signs." + counter + ".World", loc.getWorld().getName());
        plugin.arenas.set("Arenas." + Arena + ".Signs.Counter", counter);
        Methods.saveYamls();
    }

    public void removeSign(final Location loc) {
        final String ArenaName = getName();
        for (int count = 1; plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + count + ".X"); ++count) {
            final Location loc2 = new Location(Bukkit.getWorld(plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + count + ".World")), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".X"), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Y"), plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + count + ".Z"));
            if (loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ()) {
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".X", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".Y", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".Z", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count + ".World", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + count, null);
                resetSigns();
                Methods.saveYamls();
                break;
            }
        }
    }

    private void resetSigns() {
        final String ArenaName = getName();
        int newCount = 0;
        for (int counter = plugin.arenas.getInt("Arenas." + ArenaName + ".Signs.Counter"), i = 0; i <= counter; ++i) {
            if (plugin.arenas.contains("Arenas." + ArenaName + ".Signs." + i + ".X")) {
                ++newCount;
                final double x = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".X");
                final double y = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".Y");
                final double z = plugin.arenas.getDouble("Arenas." + ArenaName + ".Signs." + i + ".Z");
                final String world = plugin.arenas.getString("Arenas." + ArenaName + ".Signs." + i + ".World");
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".X", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".Y", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".Z", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i + ".World", null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + i, null);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".X", x);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".Y", y);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".Z", z);
                plugin.arenas.addDefault("Arenas." + ArenaName + ".Signs." + newCount + ".World", world);
                plugin.arenas.set("Arenas." + ArenaName + ".Signs.Counter", newCount);
            }
        }
    }

    public boolean hasPlayer(final Player player) {
        return players.contains(player.getUniqueId());
    }

    public void addPlayer(final Player player) {
        if (!players.contains(player.getUniqueId())) {
            final Location loc = this.getLobbySpawn();
            if (loc != null) {
                player.teleport(loc);
                players.add(player.getUniqueId());
                Arenas.addArena(player, this);
                sendAll(color(player.getName() + settings.getConfig().getString("Messages.Join")));
                player.sendTitle(ChatColor.DARK_RED + "<< " + ChatColor.GOLD + "UltimaRoyale " + ChatColor.DARK_RED + ">>", "");
                saveInventory(player);
                Methods.setLobbyInventory(player);
                player.updateInventory();
                player.setHealth(20.0);
                player.setFoodLevel(20);
                final String playerName = player.getName();
                final File userdata = new File(plugin.getDataFolder(), File.separator + "players");
                final File f = new File(userdata, File.separator + playerName + ".yml");
                final FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
                if (f.exists()) {
                    try {
                        playerData.set("lobby", true);
                        playerData.save(f);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                if (getState() == GameState.IN_GAME) {
                    player.setScoreboard(scoreboard);
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                }
                if (this.canStart()) {
                    this.start();
                }
            } else {
                UltimaRoyale.sendMessage(player, "lobby does not exist.");
            }
            this.updateSigns();
        }
    }

    public void playerSpec(final Player player) {
    }

    public void removePlayer(final Player player, final LeaveReason reason) {
        players.remove(player.getUniqueId());
        player.teleport(Methods.getLobby());
        UltimaRoyale.sendMessage(player, "Back to the lobby.");
        this.updateSigns();
        this.loadInventory(player);
        if (reason == LeaveReason.QUIT) {
            this.sendAll(color(settings.getConfig().getString("Messages.Left")));
        }
        if (reason == LeaveReason.KICK) {
            this.sendAll(ChatColor.RED + player.getName() + ChatColor.GRAY + " got kicked.");
        }
        if (reason == LeaveReason.DEATHS) {
            this.sendAll(ChatColor.RED + player.getName() + ChatColor.GRAY + " died!");
        }
        if (reason == LeaveReason.STOPPED) {
            this.sendAll(ChatColor.GREEN + "oh :)");
        }
        player.setMaxHealth(20.0);
        player.setHealth(20.0);
        Arenas.removeArena(player);
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        if (player.isInsideVehicle()) {
            player.getVehicle().eject();
        }
        for (final Player ps : Bukkit.getServer().getOnlinePlayers()) {
            ps.showPlayer(player);
        }
        if ((getState() == GameState.IN_GAME || getState() == GameState.STARTING) && players.size() <= 1) {
            this.stop();
        }
    }

    public int getKillsToWin() {
        return plugin.getConfig().getInt(getName() + ".KillsToWin");
    }

    public int getMaxPlayers() {
        return plugin.getConfig().getInt(getName() + ".MaxPlayers");
    }

    public int getAutoStartPlayers() {
        return plugin.getConfig().getInt(getName() + ".AutoStartPlayers");
    }

    public boolean canStart() {
        return getState() != GameState.IN_GAME && getState() != GameState.STARTING && getState() != GameState.STOPPING && players.size() >= getAutoStartPlayers();
    }
}