package com.nocheeterna.darklevel;

import com.nocheeterna.NocheEterna;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class DarkLevelListener implements org.bukkit.event.Listener {

    private final NocheEterna plugin;

    public DarkLevelListener(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasPermission("nocheeterna.bypass")) return;
        double deathGain = plugin.getConfigManager().getDeathGain();
        plugin.getDarkLevelManager().addDarkLevel(player.getUniqueId(), deathGain);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.hasPermission("nocheeterna.bypass")) return;
        double damageGain = plugin.getConfigManager().getDamageGain();
        double hearts = event.getFinalDamage() / 2.0;
        plugin.getDarkLevelManager().addDarkLevel(player.getUniqueId(), hearts * damageGain);
    }
}
