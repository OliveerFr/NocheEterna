package com.nocheeterna.network;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.events.LevelChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NetworkJoinListener implements Listener {

    private final NocheEterna plugin;
    private final NetworkSyncManager sync;

    public NetworkJoinListener(NocheEterna plugin, NetworkSyncManager sync) {
        this.plugin = plugin;
        this.sync = sync;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nocheeterna.bypass")) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            sync.requestLevel(player.getUniqueId());
        }, 40L);
    }

    @EventHandler
    public void onLevelChange(LevelChangeEvent event) {
        if (event.getDelta() == 0) return;
        if (Math.abs(event.getDelta()) < 0.5) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double level = plugin.getDarkLevelManager().getDarkLevel(event.getPlayerUuid());
            sync.broadcastLevel(event.getPlayerUuid(), level);
        }, 5L);
    }
}
