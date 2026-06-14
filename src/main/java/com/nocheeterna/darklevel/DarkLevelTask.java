package com.nocheeterna.darklevel;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.core.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DarkLevelTask extends BukkitRunnable {

    private final NocheEterna plugin;
    private int saveCounter = 0;

    public DarkLevelTask(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!plugin.getConfigManager().isEnabled()) return;

        ConfigManager cfg = plugin.getConfigManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("nocheeterna.bypass")) continue;

            UUID uuid = player.getUniqueId();
            double gain = 0.0;

            if (cfg.isDebug()) {
                gain += 0; // base passive is added below
            }

            gain += cfg.getPassiveGain();

            World world = player.getWorld();
            boolean isNight = world.getEnvironment() == World.Environment.NORMAL
                    && world.getTime() >= 13000 && world.getTime() <= 23000;
            boolean isCave = player.getLocation().getY() <= 40
                    && player.getLocation().getBlock().getLightFromSky() == 0;
            boolean isSurfaceDay = player.getLocation().getY() >= 64
                    && player.getLocation().getBlock().getLightFromSky() > 10
                    && !isNight;

            if (isNight) gain += cfg.getNightGain();
            if (isCave) gain += cfg.getCaveGain();
            if (isSurfaceDay) gain -= cfg.getSurfaceDecay();

            if (gain > 0) {
                plugin.getDarkLevelManager().addDarkLevel(uuid, gain);
            } else if (gain < 0) {
                plugin.getPlayerDataManager().removeDarkLevel(uuid, Math.abs(gain));
            }
        }

        saveCounter++;
        if (saveCounter >= (cfg.getSaveInterval() / 600)) {
            saveCounter = 0;
            plugin.getPlayerDataManager().saveData();
            if (cfg.isDebug()) {
                plugin.getLogger().info("[DEBUG] Player data saved.");
            }
        }
    }
}
