package com.nocheeterna.darklevel;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.events.LevelChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DarkLevelListener implements Listener {

    private final NocheEterna plugin;

    public DarkLevelListener(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("nocheeterna.bypass")) return;

        double oldLevel = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
        String oldPhase = plugin.getDarkLevelManager().getPhase(oldLevel);
        double deathGain = plugin.getConfigManager().getDeathGain();

        plugin.getPlayerDataManager().addDarkLevel(player.getUniqueId(), deathGain);
        double newLevel = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());

        plugin.getServer().getPluginManager().callEvent(
                new LevelChangeEvent(player.getUniqueId(), oldLevel, newLevel,
                        LevelChangeEvent.ChangeCause.DEATH));

        String newPhase = plugin.getDarkLevelManager().getPhase(newLevel);
        if (!oldPhase.equals(newPhase)) {
            plugin.getServer().getPluginManager().callEvent(
                    new DarkPhaseChangeEvent(player.getUniqueId(), oldPhase, newPhase));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasPermission("nocheeterna.bypass")) return;

        double damageGain = plugin.getConfigManager().getDamageGain();
        double hearts = event.getFinalDamage() / 2.0;
        if (hearts <= 0) return;

        double oldLevel = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
        String oldPhase = plugin.getDarkLevelManager().getPhase(oldLevel);

        plugin.getPlayerDataManager().addDarkLevel(player.getUniqueId(), hearts * damageGain);
        double newLevel = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());

        plugin.getServer().getPluginManager().callEvent(
                new LevelChangeEvent(player.getUniqueId(), oldLevel, newLevel,
                        LevelChangeEvent.ChangeCause.DAMAGE));

        String newPhase = plugin.getDarkLevelManager().getPhase(newLevel);
        if (!oldPhase.equals(newPhase)) {
            plugin.getServer().getPluginManager().callEvent(
                    new DarkPhaseChangeEvent(player.getUniqueId(), oldPhase, newPhase));
        }
    }
}
