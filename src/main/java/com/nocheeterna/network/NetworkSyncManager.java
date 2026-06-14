package com.nocheeterna.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nocheeterna.NocheEterna;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetworkSyncManager implements PluginMessageListener {

    private final NocheEterna plugin;
    private final Map<UUID, Long> pendingRequests = new HashMap<>();
    private static final String CHANNEL_BC = "BungeeCord";
    private static final String CHANNEL_NETWORK = "onminetwork:main";
    private static final String SUBCHANNEL = "nocheeterna:darkness";

    public NetworkSyncManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL_BC);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, CHANNEL_NETWORK, this);
        plugin.getLogger().info("[Network] Registered on 'onminetwork:main'.");
    }

    public void unregister() {
        try {
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL_BC);
            Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL_NETWORK);
        } catch (Exception ignored) {}
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL_NETWORK)) return;

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            if (!subchannel.equals(SUBCHANNEL)) return;

            String action = in.readUTF();
            String payload = in.readUTF();

            switch (action) {
                case "SET" -> handleSet(payload);
                case "REQ" -> handleRequest(payload);
                case "RES" -> handleResponse(payload);
            }
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("[Network] PM parse error: " + e.getMessage());
            }
        }
    }

    /**
     * Broadcast a player's dark level to ALL servers in the network.
     */
    public void broadcastLevel(UUID playerUuid, double level) {
        broadcast("SET", playerUuid + ":" + String.format("%.2f", level));
    }

    /**
     * Request the latest dark level for a player from all servers.
     * Call on player join.
     */
    public void requestLevel(UUID playerUuid) {
        pendingRequests.put(playerUuid, System.currentTimeMillis());
        broadcast("REQ", playerUuid.toString());
        Bukkit.getScheduler().runTaskLater(plugin, () -> pendingRequests.remove(playerUuid), 100L);
    }

    private void handleRequest(String payload) {
        try {
            UUID uuid = UUID.fromString(payload.trim());
            double level = plugin.getDarkLevelManager().getDarkLevel(uuid);
            if (level > 0) {
                broadcast("RES", uuid + ":" + String.format("%.2f", level));
            }
        } catch (IllegalArgumentException ignored) {}
    }

    private void handleResponse(String payload) {
        String[] parts = payload.split(":");
        if (parts.length < 2) return;
        try {
            UUID uuid = UUID.fromString(parts[0]);
            double level = Double.parseDouble(parts[1]);
            if (!pendingRequests.containsKey(uuid)) return;
            pendingRequests.remove(uuid);

            double current = plugin.getDarkLevelManager().getDarkLevel(uuid);
            if (level > current) {
                plugin.getDarkLevelManager().setDarkLevel(uuid, level);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    private void handleSet(String payload) {
        String[] parts = payload.split(":");
        if (parts.length < 2) return;
        try {
            UUID uuid = UUID.fromString(parts[0]);
            double level = Double.parseDouble(parts[1]);
            double current = plugin.getDarkLevelManager().getDarkLevel(uuid);
            if (Math.abs(level - current) > 0.01) {
                plugin.getDarkLevelManager().setDarkLevel(uuid, level);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    /**
     * Broadcast a message to all servers via BungeeCord Plugin Messaging.
     * BungeeCord receives on "BungeeCord" channel, then forwards
     * the payload to all servers on "onminetwork:main".
     */
    private void broadcast(String action, String payload) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(CHANNEL_NETWORK);
        out.writeUTF(SUBCHANNEL);
        out.writeUTF(action);
        out.writeUTF(payload);

        Player any = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
        if (any != null) {
            any.sendPluginMessage(plugin, CHANNEL_BC, out.toByteArray());
        }
    }
}
