package ml.matrixdevteam.ultimaroyale.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class FileManager {
    static FileManager instance;

    static {
        instance = new FileManager();
    }

    private FileManager() {
    }

    public static FileManager getInstance() {
        return instance;
    }

    public void setup(final Plugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        Config.reload();
        Config.load();
        Config.save();
        Config.reload();
    }

    public FileConfiguration getConfig() {
        return Config.getConfig();
    }

    public void saveConfig() {
        Config.save();
    }

    public void reloadConfig() {
        Config.reload();
    }
}
