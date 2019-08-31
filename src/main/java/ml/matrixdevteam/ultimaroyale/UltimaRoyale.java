package ml.matrixdevteam.ultimaroyale;

import de.slikey.effectlib.EffectManager;
import ml.matrixdevteam.ultimaroyale.arena.Arena;
import ml.matrixdevteam.ultimaroyale.arena.Arenas;
import ml.matrixdevteam.ultimaroyale.arena.LeaveReason;
import ml.matrixdevteam.ultimaroyale.listeners.AchievementListener;
import ml.matrixdevteam.ultimaroyale.listeners.FallListener;
import ml.matrixdevteam.ultimaroyale.listeners.GameListener;
import ml.matrixdevteam.ultimaroyale.listeners.MenuListener;
import ml.matrixdevteam.ultimaroyale.util.FileManager;
import ml.matrixdevteam.ultimaroyale.util.Methods;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class UltimaRoyale extends JavaPlugin implements Listener {
    private static UltimaRoyale instance;
    public final Logger logger;
    public final Methods methods;
    public final GameListener gameListener;
    public final FallListener fallListener;
    public final MenuListener menuListener;
    public final AchievementListener achievementListener;
    public static FileManager settings = FileManager.getInstance();
    public static EffectManager effectManager;
    private Boolean everyone;
    public File loadoutsFile, playersFile, arenasFile;
    public static FileConfiguration loadouts, players;
    public FileConfiguration arenas;
    public static Economy eco = null;

    public UltimaRoyale() {
        this.logger = Logger.getLogger("Minecraft");
        this.methods = new Methods(this);
        this.gameListener = new GameListener(this);
        this.fallListener = new FallListener(this);
        this.menuListener = new MenuListener(this);
        this.achievementListener = new AchievementListener(this);
        this.everyone = false;
    }

    public static FileConfiguration getLoadoutsFile() {
        return loadouts;
    }

    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        }

        return eco != null;
    }

    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            logger.info("Vault detected, continuing.");
        } else {
            logger.severe("Vault plugin not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
        if (getServer().getPluginManager().getPlugin("EffectLib") != null) {
            logger.info("EffectLib detected, continuing.");
        } else {
            logger.severe("EffectLib plugin not found, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
        try {
            if (Class.forName("net.minecraftforge.common.ForgeVersion") != null) {
                logger.severe("You're using a hybrid minecraft server, this is not officially supported at this time, disabling.");
                getServer().getPluginManager().disablePlugin(this);
            }
        } catch (ClassNotFoundException ignored) {
        }

        logger.info("Loading Configuration Files!");
        settings.setup(this);
        arenasFile = new File(getDataFolder(), "arenas.yml");

        arenas = new YamlConfiguration();
        Methods.loadYamls();
        arenas.options().copyDefaults(true);
        getConfig().options().copyDefaults(true);
        effectManager = new EffectManager(this);
        logger.info("Configurations loaded successfully!");
        getServer().getPluginManager().registerEvents(gameListener, this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(fallListener, this);
        getServer().getPluginManager().registerEvents(menuListener, this);
        getServer().getPluginManager().registerEvents(achievementListener, this);
        try {
            for (final String s : arenas.getStringList("Arenas.List")) {
                final Arena arena = new Arena(s);
                logger.info("UltimaRoyale > Loading Arena: " + arena.getName());
                Arenas.addArena(arena);
                arena.updateSigns();
                logger.info("UltimaRoyale > Arena: " + arena.getName() + " loaded!");
            }
        } catch (Exception ignored) {
        }
        try {
            methods.firstRun();
        } catch (Exception ignored) {
        }
        Methods.loadYamls();
        super.onEnable();
    }

    public void onDisable() {
        for (final Arena arena : Arenas.getArenas().values()) {
            arena.stop();
        }
        HandlerList.unregisterAll((Listener) this);
        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((label.equalsIgnoreCase("battleroyale") && !(sender instanceof Player))) {
            sender.sendMessage("You need to be a player to perform this command!");
        }

        if ((label.equalsIgnoreCase("battleroyale"))) {
            final Player player = (Player) sender;
            if (!Arenas.isInArena(player) && args.length == 0) {
                sendMessage(player, "Incorrect command, use '/battleroyale help' for help");
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    StringBuilder arenas = new StringBuilder("Arena list: " + ChatColor.YELLOW);
                    for (final Arena arena : Arenas.getArenas().values()) {
                        arenas.append(arena.getName()).append(", ");
                    }
                    sendMessage(player, arenas.toString());
                } else if (args[0].equalsIgnoreCase("version")) {
                    sendMessage(player, "Plugin version is &cv" + getDescription().getVersion());
                } else if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.GRAY + "------------------" + ChatColor.GOLD + ChatColor.BOLD + " UltimaRoyale " + ChatColor.GRAY + "------------------");
                    sender.sendMessage("/br setmainlobby" + ChatColor.GRAY + " - Set the main lobby");
                    sender.sendMessage("/br create <ArenaName> " + ChatColor.GRAY + " - Create an Arena");
                    sender.sendMessage("/br setlobby <ArenaName> " + ChatColor.GRAY + " - Set the arena lobby");
                    sender.sendMessage("/br addspawn <ArenaName> " + ChatColor.GRAY + " - Add spawn to your arena");
                    sender.sendMessage(ChatColor.GRAY + "------------------" + ChatColor.GOLD + ChatColor.BOLD + " UltimaRoyale " + ChatColor.GRAY + "------------------");
                }

                if (player.hasPermission("ultimaroyale.admin")) {
                    if (args[0].equalsIgnoreCase("setmainlobby")) {
                        Methods.setLobby(player.getLocation());
                        sendMessage(player, "The main lobby has been set to your location!");
                    } else if (args[0].equalsIgnoreCase("reload")) {
                        Methods.loadYamls();
                        reloadConfig();
                        for (final Arena arena : Arenas.getArenas().values()) {
                            arena.updateSigns();
                        }

                        sendMessage(player, "Config files reloaded!");
                    }
                }

                if (args[0].equalsIgnoreCase("lobby")) {
                    if (!Arenas.isInArena(player)) {
                        if (Methods.getLobby() != null) {
                            player.teleport(Methods.getLobby());
                            sendMessage(player, "Welcome to the main waiting lobby!");
                        } else {
                            sendMessage(player, "The lobby does not exist, use '/br list' to view the available arenas.");
                        }
                    } else {
                        final Arena arena = Arenas.getArena(player);
                        sendMessage(player, "You have left the game, and have been spawned in the lobby.");
                        arena.removePlayer(player, LeaveReason.QUIT);
                    }
                }
            }

            if (args.length == 2 && player.hasPermission("ultimaroyale.admin")) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (!Arenas.arenaExists(args[1])) {
                        arenas.addDefault("Arenas." + args[1], args[1]);
                        arenas.addDefault("Arenas." + args[1] + ".Signs.Counter", 0);
                        getConfig().addDefault(args[1] + ".Countdown", 15);
                        getConfig().addDefault(args[1] + ".MaxPlayers", 8);
                        getConfig().addDefault(args[1] + ".AutoStartPlayers", 2);
                        getConfig().addDefault(args[1] + ".EndTime", 600);
                        final Arena arena = new Arena(args[1]);
                        Arenas.addArena(arena);
                        Methods.addToList(arena);
                        sendMessage(player, ChatColor.GRAY + "You've created arena: &c" + arena.getName());
                        Methods.saveYamls();
                        saveConfig();
                    } else {
                        sendMessage(player, ChatColor.RED + "An arena with that name already exists!");
                    }
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (getConfig().contains(args[1])) {
                        getConfig().set(args[1], null);
                        arenas.set("Arenas." + args[1], null);
                        Methods.removeFromList(args[1]);
                        Methods.saveYamls();
                        sendMessage(player, "You've deleted arena: &c" + args[1]);
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        arena.addPlayer(player);
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("start")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        if (arena.getPlayers().size() >= 2) {
                            arena.start();
                            sendMessage(player, "You've forced the game to start in arena &c" + arena.getName());
                        } else {
                            sendMessage(player, "The arena cannot be started as there are not enough players.");
                        }
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("stop")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        arena.sendAll("A staff member, " + player.getDisplayName() + " has stopped the game!");
                        arena.stop();
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        arena.addSpawn(player.getLocation());
                        sendMessage(player, "A spawn has been set to your location in arena &e" + arena.getName());
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("addchest")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        arena.addChest(player.getLocation());
                        sendMessage(player, "A chest spawn has been set to your location in arena &e" + arena.getName());
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    if (Arenas.arenaExists(args[1])) {
                        final Arena arena = Arenas.getArena(args[1]);
                        arena.setLobbySpawn(player.getLocation());
                        sendMessage(player, "A lobby has been set to your location in arena &e" + arena.getName());
                    } else {
                        sendMessage(player, "Arena &c" + args[1] + "&f doesn't exist, use '/br list' to view existing arenas.");
                    }
                }
            }
        }

        return super.onCommand(sender, command, label, args);
    }

    public static void sendMessage(final Player player, final String Message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("Messages.Prefix") + Message));
    }

    public static UltimaRoyale getInstance() {
        return instance;
    }
}