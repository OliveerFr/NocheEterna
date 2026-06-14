package com.nocheeterna.network;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.core.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.net.InetAddress;
import java.util.*;

public class NetworkSecurityManager implements Listener, PluginMessageListener {

    private final NocheEterna plugin;
    private final Map<UUID, List<Long>> commandTimestamps = new HashMap<>();

    public NetworkSecurityManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        ConfigManager cfg = plugin.getConfigManager();
        if (cfg.isBungeeCordEnabled()) {
            plugin.getServer().getMessenger().registerIncomingPluginChannel(
                    plugin, "BungeeCord", this);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        ConfigManager cfg = plugin.getConfigManager();

        if (cfg.isBungeeCordEnabled() && cfg.isEnforceIpForward()) {
            InetAddress addr = event.getAddress();
            String host = addr.getHostAddress();
            if (!isFromProxy(host)) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        "\u00a7cUnauthorized connection. Use the proxy to join.");
                plugin.getLogger().warning("Rejected direct connection from: " + host);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nocheeterna.bypass")) return;

        ConfigManager cfg = plugin.getConfigManager();
        int max = cfg.getMaxCommands();
        int window = cfg.getRateWindowSeconds();

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long windowMs = window * 1000L;

        List<Long> timestamps = commandTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());
        timestamps.add(now);
        timestamps.removeIf(t -> t < now - windowMs);

        if (timestamps.size() > max) {
            event.setCancelled(true);
            player.sendMessage(cfg.getPrefix() + "\u00a7cSlow down! Command rate limit reached.");
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;

        ConfigManager cfg = plugin.getConfigManager();
        if (!cfg.isBungeeCordEnabled()) return;

        if (message.length < 1) return;

        try {
            java.io.DataInputStream in = new java.io.DataInputStream(
                    new java.io.ByteArrayInputStream(message));
            String subChannel = in.readUTF();

            if (subChannel.equals("ServerName")) {
                if (cfg.isDebug()) {
                    plugin.getLogger().info("[DEBUG] Proxy server name received for "
                            + player.getName());
                }
            }
        } catch (Exception e) {
            if (cfg.isDebug()) {
                plugin.getLogger().warning("[DEBUG] Error reading BungeeCord message: "
                        + e.getMessage());
            }
        }
    }

    private boolean isFromProxy(String hostAddress) {
        return hostAddress.equals("127.0.0.1")
                || hostAddress.equals("0:0:0:0:0:0:0:1")
                || hostAddress.equals("localhost");
    }

    public void cleanup(UUID uuid) {
        commandTimestamps.remove(uuid);
    }
}
