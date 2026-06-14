package com.nocheeterna.horror;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkLevelManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class AmbientHorrorManager {

    private final NocheEterna plugin;
    private final Random random = new Random();
    private BukkitTask soundTask;
    private BukkitTask particleTask;

    public AmbientHorrorManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void start() {
        startSoundTask();
        startParticleTask();
    }

    public void stop() {
        if (soundTask != null) soundTask.cancel();
        if (particleTask != null) particleTask.cancel();
    }

    private void startSoundTask() {
        if (!plugin.getConfigManager().isSoundsEnabled()) return;

        int interval = plugin.getConfig().getInt("ambient.sounds.interval-ticks", 1200);

        soundTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("nocheeterna.bypass")) continue;
                    String phase = plugin.getDarkLevelManager().getPhase(player.getUniqueId());
                    playAmbientSound(player, phase);
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    private void startParticleTask() {
        if (!plugin.getConfigManager().isParticlesEnabled()) return;

        int interval = plugin.getConfig().getInt("ambient.particles.interval-ticks", 40);

        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("nocheeterna.bypass")) continue;
                    String phase = plugin.getDarkLevelManager().getPhase(player.getUniqueId());
                    spawnParticles(player, phase);
                }
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    public void playPhaseAmbientSound(Player player, String phase) {
        playAmbientSound(player, phase);
    }

    private void playAmbientSound(Player player, String phase) {
        List<String> sounds = plugin.getConfig().getStringList("ambient.sounds.sounds." + phase);
        if (sounds == null || sounds.isEmpty()) return;

        String entry = sounds.get(random.nextInt(sounds.size()));
        String[] parts = entry.split(":");
        try {
            Sound sound = Sound.valueOf(parts[0]);
            float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
            Location loc = randomLocationNear(player, 10);
            player.playSound(loc, sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("[DEBUG] Invalid sound entry: " + entry);
            }
        }
    }

    private void spawnParticles(Player player, String phase) {
        String entry = plugin.getConfig().getString("ambient.particles.types." + phase, "ASH:1:0.2");
        String[] parts = entry.split(":");
        try {
            Particle particle = Particle.valueOf(parts[0]);
            int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            double offset = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.5;
            Location loc = randomLocationNear(player, 5);
            player.spawnParticle(particle, loc, count, offset, offset, offset, 0.02);
        } catch (IllegalArgumentException e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("[DEBUG] Invalid particle entry: " + entry);
            }
        }
    }

    private Location randomLocationNear(Player player, int radius) {
        Location base = player.getLocation();
        double angle = random.nextDouble() * Math.PI * 2;
        double dist = 3 + random.nextDouble() * radius;
        double x = base.getX() + Math.cos(angle) * dist;
        double z = base.getZ() + Math.sin(angle) * dist;
        double y = base.getY() + (random.nextDouble() - 0.5) * 4;
        return new Location(base.getWorld(), x, y, z);
    }

    public void applyFogEffect(Player player, String phase) {
        if (!plugin.getConfigManager().isFogEnabled()) return;
        int duration = 999999;
        int amplifier = switch (phase) {
            case "twilight" -> 0;
            case "night" -> 0;
            case "nightmare" -> 1;
            case "abyss" -> 2;
            default -> -1;
        };
        if (amplifier >= 0) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.DARKNESS,
                    duration, amplifier, false, false));
        }
    }
}
