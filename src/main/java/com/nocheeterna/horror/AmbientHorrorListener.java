package com.nocheeterna.horror;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkPhaseChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AmbientHorrorListener implements Listener {

    private final NocheEterna plugin;

    public AmbientHorrorListener(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPhaseChange(DarkPhaseChangeEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            org.bukkit.entity.Player player = Bukkit.getPlayer(event.getPlayerUuid());
            if (player != null && player.isOnline()) {
                if (plugin.getConfigManager().isFogEnabled()) {
                    plugin.getAmbientHorrorManager().applyFogEffect(player, event.getNewPhase());
                }
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            org.bukkit.entity.Player player = event.getPlayer();
            if (player.hasPermission("nocheeterna.bypass")) return;
            String phase = plugin.getDarkLevelManager().getPhase(player.getUniqueId());
            if (plugin.getConfigManager().isFogEnabled()) {
                plugin.getAmbientHorrorManager().applyFogEffect(player, phase);
            }
        }, 10L);
    }
}
