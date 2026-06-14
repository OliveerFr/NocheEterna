package com.nocheeterna.darklevel;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.core.ConfigManager;

import java.util.UUID;

public class DarkLevelManager {

    private final NocheEterna plugin;

    public DarkLevelManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public double getDarkLevel(UUID uuid) {
        return plugin.getPlayerDataManager().getDarkLevel(uuid);
    }

    public void setDarkLevel(UUID uuid, double level) {
        double oldLevel = getDarkLevel(uuid);
        String oldPhase = getPhase(oldLevel);
        plugin.getPlayerDataManager().setDarkLevel(uuid, level);
        String newPhase = getPhase(level);

        if (!oldPhase.equals(newPhase)) {
            onPhaseChange(uuid, oldPhase, newPhase);
        }
    }

    public void addDarkLevel(UUID uuid, double amount) {
        setDarkLevel(uuid, getDarkLevel(uuid) + amount);
    }

    public String getPhase(double level) {
        ConfigManager cfg = plugin.getConfigManager();
        if (level >= 81) return "abyss";
        if (level >= 51) return "nightmare";
        if (level >= 21) return "night";
        return "twilight";
    }

    public String getPhase(UUID uuid) {
        return getPhase(getDarkLevel(uuid));
    }

    private void onPhaseChange(UUID uuid, String oldPhase, String newPhase) {
        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) return;

            String phaseName = plugin.getConfigManager().getPhaseName(newPhase);
            String msg = plugin.getConfigManager().getMessage("phase-change")
                    .replace("%s", phaseName);
            player.sendMessage(msg);

            org.bukkit.Bukkit.getPluginManager().callEvent(
                    new com.nocheeterna.darklevel.DarkPhaseChangeEvent(uuid, oldPhase, newPhase));

            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("[DEBUG] " + player.getName()
                        + " phase change: " + oldPhase + " -> " + newPhase
                        + " (level: " + getDarkLevel(uuid) + ")");
            }
        });
    }
}
