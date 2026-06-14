package com.nocheeterna.ux;

import com.nocheeterna.NocheEterna;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerWelcome implements Listener {

    private final NocheEterna plugin;

    public PlayerWelcome(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
                player.sendMessage("\u00a75\u2726  Noche Eterna");
                player.sendMessage("\u00a77     La oscuridad te acecha... cada accion cuenta.");
                player.sendMessage("\u00a77     \u00a7eDe dia\u00a77: tu nivel de oscuridad disminuye lentamente.");
                player.sendMessage("\u00a77     \u00a78De noche\u00a77: la oscuridad crece.");
                player.sendMessage("\u00a77     \u00a77En cuevas\u00a77: el terror se intensifica.");
                player.sendMessage("\u00a77     \u00a75/mira tu nivel\u00a77: /noche level");
                player.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
            }, 60L);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            double level = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
            String phase = plugin.getDarkLevelManager().getPhase(level);
            String phaseName = plugin.getConfigManager().getPhaseName(phase);
            player.sendMessage(plugin.getConfigManager().getPrefix()
                    + "\u00a77Tu nivel actual: \u00a75" + String.format("%.1f", level)
                    + "\u00a78/\u00a77100 \u00a78(" + phaseName + "\u00a78)");
        }, 80L);
    }
}
