package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkPhaseChangeEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

public class ProtocolLibEffects implements Listener {

    private final NocheEterna plugin;
    private final Random random = new Random();
    private ProtocolManager protocolManager;

    private final List<String> fakeNightmareMessages = List.of(
            "\u00a78\u00a7o...no estas solo...",
            "\u00a78\u00a7oAlguien respira detras de ti",
            "\u00a78\u00a7oTus ojos te mienten...",
            "\u00a78\u00a7oElla esta en las paredes",
            "\u00a78\u00a7oNo mires atras",
            "\u00a78\u00a7oYa es demasiado tarde",
            "\u00a78\u00a7o\u00bfPor que sigues jugando?",
            "\u00a78\u00a7oTe estamos viendo..."
    );

    private final List<String> fakeAbyssMessages = List.of(
            "\u00a74\u00a7oNO HAY SALIDA",
            "\u00a74\u00a7oEL ABISMO TE MIRA FIJO",
            "\u00a74\u00a7o\u00a7lTU ALMA ES NUESTRA",
            "\u00a74\u00a7o\u00a7k\u00a7lNO ESCAPARAS\u00a7r\u00a74",
            "\u00a74\u00a7o\u00bfCREISTE QUE ERA UN JUEGO?",
            "\u00a74\u00a7o\u00a7l\u00a7k\u00a7nLLEGO LA NOCHE ETERNA"
    );

    public ProtocolLibEffects(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            return false;
        }
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("[Integration] ProtocolLib hooked - horror packets ready.");
        return true;
    }

    @EventHandler
    public void onPhaseChange(DarkPhaseChangeEvent event) {
        String phase = event.getNewPhase();
        Player player = Bukkit.getPlayer(event.getPlayerUuid());
        if (player == null || !player.isOnline()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            switch (phase) {
                case "nightmare" -> sendFakeChat(player, fakeNightmareMessages);
                case "abyss" -> {
                    sendFakeChat(player, fakeAbyssMessages);
                    sendFakeDisconnect(player);
                }
            }
        }, 40L);
    }

    private void sendFakeChat(Player player, List<String> messages) {
        String msg = messages.get(random.nextInt(messages.size()));
        PacketContainer chat = protocolManager.createPacket(PacketType.Play.Server.SYSTEM_CHAT);
        chat.getChatComponents().write(0, WrappedChatComponent.fromText(msg));
        chat.getBooleans().write(0, false);
        sendPacket(player, chat);
    }

    private void sendFakeDisconnect(Player player) {
        if (random.nextInt(3) != 0) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendTitle(
                    "\u00a74\u00a7lNOCHE ETERNA",
                    "\u00a7cTu conexion se ha corrompido...",
                    5, 40, 10);
        }, 100L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage("\u00a78\u00a7o[... Todo sigue igual... pero no es igual]");
        }, 160L);
    }

    private void sendPacket(Player player, PacketContainer packet) {
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("[ProtocolLib] Failed to send packet: " + e.getMessage());
            }
        }
    }
}
