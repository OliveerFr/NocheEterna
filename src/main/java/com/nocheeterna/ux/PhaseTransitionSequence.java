package com.nocheeterna.ux;

import com.nocheeterna.NocheEterna;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PhaseTransitionSequence {

    public static void play(NocheEterna plugin, Player player, String oldPhase, String newPhase) {
        String phaseName = plugin.getConfigManager().getPhaseName(newPhase);
        String msg = plugin.getConfigManager().getMessage("phase-change")
                .replace("%s", phaseName);

        switch (newPhase) {
            case "night" -> playNightTransition(plugin, player, msg);
            case "nightmare" -> playNightmareTransition(plugin, player, msg);
            case "abyss" -> playAbyssTransition(plugin, player, msg);
            default -> player.sendMessage(msg);
        }
    }

    private static void playNightTransition(NocheEterna plugin, Player player, String msg) {
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 0.5f, 0.7f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendTitle("\u00a77...", "\u00a78la noche avanza", 10, 30, 10);
        }, 5L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(msg);
        }, 25L);
    }

    private static void playNightmareTransition(NocheEterna plugin, Player player, String msg) {
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_EMERGE, 0.3f, 0.8f);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendTitle("\u00a75\u2726 Pesadilla \u2726", "\u00a78la oscuridad se vuelve real", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 0.4f, 0.6f);
        }, 10L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(msg);
        }, 40L);
    }

    private static void playAbyssTransition(NocheEterna plugin, Player player, String msg) {
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_ANGRY, 0.6f, 0.3f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 0.3f, 0.2f);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendTitle("\u00a74\u00a7lABISMO", "\u00a7c\u00a7ola noche eterna te consume", 5, 50, 15);
            player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.1f);
        }, 10L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(msg);
            player.getWorld().strikeLightningEffect(player.getLocation());
        }, 50L);
    }
}
