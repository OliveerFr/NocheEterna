package com.nocheeterna.core;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkLevelManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final NocheEterna plugin;
    private final File dataFile;
    private final YamlConfiguration yaml;
    private final Map<UUID, Double> darkLevels = new HashMap<>();

    public PlayerDataManager(NocheEterna plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "player_data.yml");
        this.yaml = new YamlConfiguration();
    }

    public void loadData() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create player_data.yml: " + e.getMessage());
            }
            return;
        }

        try {
            yaml.load(dataFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load player_data.yml: " + e.getMessage());
            return;
        }

        darkLevels.clear();
        for (String key : yaml.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                double level = yaml.getDouble(key, 0.0);
                darkLevels.put(uuid, level);
            } catch (IllegalArgumentException ignored) {
            }
        }

        plugin.getLogger().info("Loaded dark levels for " + darkLevels.size() + " players.");
    }

    public void saveData() {
        for (Map.Entry<UUID, Double> entry : darkLevels.entrySet()) {
            yaml.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            yaml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player_data.yml: " + e.getMessage());
        }
    }

    public double getDarkLevel(UUID uuid) {
        return darkLevels.getOrDefault(uuid, 0.0);
    }

    public void setDarkLevel(UUID uuid, double level) {
        double max = plugin.getConfig().getDouble("dark-level.max", 100.0);
        level = Math.max(0.0, Math.min(level, max));
        darkLevels.put(uuid, level);
    }

    public void addDarkLevel(UUID uuid, double amount) {
        double current = getDarkLevel(uuid);
        setDarkLevel(uuid, current + amount);
    }

    public void removeDarkLevel(UUID uuid, double amount) {
        addDarkLevel(uuid, -amount);
    }
}
