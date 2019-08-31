package ml.matrixdevteam.ultimaroyale.util;

import ml.matrixdevteam.ultimaroyale.UltimaRoyale;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
    private static FileConfiguration config = null;
    private static File configFile = null;

    public static void load() {
        config = getConfig();
        config.options().header(
                "############################################################\n" +
                        "# +------------------------------------------------------+ #\n" +
                        "# |           UltimaRoyale Configuration file            | #\n " +
                        "# +------------------------------------------------------+ #\n" +
                        "############################################################");
        config.addDefault("messages.prefix", "&e&lUltimaRoyale &8> &f");
        config.addDefault("messages.join", "&f has joined the game!");
        config.addDefault("messages.left", "&f has left the game!");
        config.addDefault("messages.startInOneMin", "&c&lThe game will begin in 1 minute!");
        config.addDefault("messages.start", "&f seconds before the game begins.");
        config.addDefault("messages.started", "&f game has started, good luck!");
        config.addDefault("messages.goal", "&c&lTo win, you must be the last player standing at the end of the game.");
        config.addDefault("potion.potion1", 10.0);
        config.addDefault("potion.potion2", 20.0);

        getConfig().options().copyDefaults(true);
        save();
    }

    public static void reload() {
        if (configFile == null) {
            configFile = new File("plugins/UltimaRoyale/", "config.yml");
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static FileConfiguration getConfig() {
        if (config == null) {
            reload();
        }

        return config;
    }

    public static void save() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Couldn't save file: " + configFile.getName(), e);
        }
    }
}