package com.nocheeterna.darklevel;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.core.ConfigManager;
import com.nocheeterna.events.LevelChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DarkLevelTask extends BukkitRunnable {

    private final NocheEterna plugin;
    private int saveCounter = 0;
    private boolean isSafeZone = false;

    public DarkLevelTask(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void checkSafeZone() {
        String serverName = plugin.getConfigManager().getServerName();
        isSafeZone = plugin.getConfig().getStringList("network.safe-zones")
                .stream().anyMatch(s -> s.equalsIgnoreCase(serverName));
    }

    @Override
    public void run() {
        if (!plugin.getConfigManager().isEnabled()) return;

        ConfigManager cfg = plugin.getConfigManager();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("nocheeterna.bypass")) continue;

            var uuid = player.getUniqueId();
            double gain = 0.0;
            LevelChangeEvent.ChangeCause cause = LevelChangeEvent.ChangeCause.PASSIVE_GAIN;

            if (!isSafeZone) {
                gain += cfg.getPassiveGain();

                World world = player.getWorld();
                boolean isNight = world.getEnvironment() == World.Environment.NORMAL
                        && world.getTime() >= 13000 && world.getTime() <= 23000;
                boolean isCave = player.getLocation().getY() <= 40
                        && player.getLocation().getBlock().getLightFromSky() == 0;
                boolean isSurfaceDay = player.getLocation().getY() >= 64
                        && player.getLocation().getBlock().getLightFromSky() > 10
                        && !isNight;

                if (isNight) {
                    gain += cfg.getNightGain();
                    cause = LevelChangeEvent.ChangeCause.NIGHT;
                }
                if (isCave) {
                    gain += cfg.getCaveGain();
                    cause = LevelChangeEvent.ChangeCause.CAVE;
                }

                if (gain > 0) {
                    applyGain(player, uuid, gain, cause);
                }

                if (isSurfaceDay) {
                    applyDecay(player, uuid);
                }
            } else {
                applyDecay(player, uuid);
            }
        }

        saveCounter++;
        if (saveCounter >= (cfg.getSaveInterval() / 600)) {
            saveCounter = 0;
            plugin.getPlayerDataManager().saveData();
        }
    }

    private void applyGain(Player player, java.util.UUID uuid, double gain, LevelChangeEvent.ChangeCause cause) {
        double oldLevel = plugin.getDarkLevelManager().getDarkLevel(uuid);
        String oldPhase = plugin.getDarkLevelManager().getPhase(oldLevel);
        String newPhase = plugin.getDarkLevelManager().getPhase(oldLevel + gain);

        plugin.getPlayerDataManager().addDarkLevel(uuid, gain);
        double newLevel = plugin.getDarkLevelManager().getDarkLevel(uuid);

        plugin.getServer().getPluginManager().callEvent(
                new LevelChangeEvent(uuid, oldLevel, newLevel, cause));

        if (!oldPhase.equals(newPhase)) {
            plugin.getServer().getPluginManager().callEvent(
                    new DarkPhaseChangeEvent(uuid, oldPhase, newPhase));
        }
    }

    private void applyDecay(Player player, java.util.UUID uuid) {
        double oldLevel = plugin.getDarkLevelManager().getDarkLevel(uuid);
        if (oldLevel <= 0) return;

        double decay = plugin.getConfigManager().getSurfaceDecay();
        String oldPhase = plugin.getDarkLevelManager().getPhase(oldLevel);
        String newPhase = plugin.getDarkLevelManager().getPhase(oldLevel - decay);

        plugin.getPlayerDataManager().removeDarkLevel(uuid, decay);
        double newLevel = plugin.getDarkLevelManager().getDarkLevel(uuid);

        if (oldLevel != newLevel) {
            plugin.getServer().getPluginManager().callEvent(
                    new LevelChangeEvent(uuid, oldLevel, newLevel,
                            LevelChangeEvent.ChangeCause.SURFACE_DECAY));
        }

        if (!oldPhase.equals(newPhase)) {
            plugin.getServer().getPluginManager().callEvent(
                    new DarkPhaseChangeEvent(uuid, oldPhase, newPhase));
        }
    }
}
