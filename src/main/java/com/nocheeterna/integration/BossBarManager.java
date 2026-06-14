package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {

    private final NocheEterna plugin;
    private final ConcurrentHashMap<UUID, BossBar> playerBars = new ConcurrentHashMap<>();
    private boolean enabled = false;

    public BossBarManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", true)) return;
        enabled = true;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("nocheeterna.bypass")) continue;
                    updateBossBar(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 40L);

        plugin.getLogger().info("[Integration] BossBar indicator active.");
    }

    private void updateBossBar(Player player) {
        UUID uuid = player.getUniqueId();
        double level = plugin.getDarkLevelManager().getDarkLevel(uuid);
        String phase = plugin.getDarkLevelManager().getPhase(uuid);
        String phaseName = plugin.getConfigManager().getPhaseName(phase);

        double progress = level / 100.0;
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        BossBar bar = playerBars.computeIfAbsent(uuid, k -> {
            BossBar b = Bukkit.createBossBar(
                    "", BarColor.PURPLE, BarStyle.SOLID);
            b.addPlayer(player);
            return b;
        });

        String title = "\u00a75\u2726 Noche Eterna \u00a77"
                + String.format("%.1f", level) + "\u00a78/\u00a77100 \u00a75\u2726 "
                + phaseName;
        bar.setTitle(title);
        bar.setProgress(progress);
        bar.setColor(getBarColor(phase));
        bar.setVisible(true);
    }

    private BarColor getBarColor(String phase) {
        return switch (phase) {
            case "twilight" -> BarColor.WHITE;
            case "night" -> BarColor.BLUE;
            case "nightmare" -> BarColor.PURPLE;
            case "abyss" -> BarColor.RED;
            default -> BarColor.PURPLE;
        };
    }

    public void removePlayer(Player player) {
        BossBar bar = playerBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    public void stop() {
        playerBars.values().forEach(BossBar::removeAll);
        playerBars.clear();
    }
}
