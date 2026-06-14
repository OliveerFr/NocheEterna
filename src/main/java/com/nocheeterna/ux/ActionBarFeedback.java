package com.nocheeterna.ux;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.events.LevelChangeEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActionBarFeedback implements Listener {

    private final NocheEterna plugin;
    private final Map<UUID, String> lastPhases = new ConcurrentHashMap<>();

    public ActionBarFeedback(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void start() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("nocheeterna.bypass")) continue;
                updateActionBar(player);
            }
        }, 20L, 20L);
    }

    private void updateActionBar(Player player) {
        UUID uuid = player.getUniqueId();
        double level = plugin.getDarkLevelManager().getDarkLevel(uuid);
        String phase = plugin.getDarkLevelManager().getPhase(uuid);
        String phaseName = plugin.getConfigManager().getPhaseName(phase);

        String bar = buildProgressBar(level);
        String text = "\u00a75\u2726 " + bar + " \u00a77"
                + String.format("%.1f", level) + "\u00a78/\u00a77100 \u00a78| " + phaseName;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    @EventHandler
    public void onLevelChange(LevelChangeEvent event) {
        if (event.getDelta() == 0) return;
        Player player = Bukkit.getPlayer(event.getPlayerUuid());
        if (player == null || !player.isOnline()) return;

        String prefix = event.getDelta() > 0 ? "\u00a7c+" : "\u00a7a";
        String text = prefix + String.format("%.2f", event.getDelta())
                + " \u00a78| \u00a75" + String.format("%.1f", event.getNewLevel())
                + "\u00a78/\u00a77100";
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastPhases.remove(event.getPlayer().getUniqueId());
    }

    private String buildProgressBar(double level) {
        int filled = (int) (level / 5);
        if (filled > 20) filled = 20;
        if (filled < 0) filled = 0;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i < filled) {
                bar.append("\u00a75\u2502");
            } else {
                bar.append("\u00a78\u2502");
            }
        }
        return bar.toString();
    }
}
