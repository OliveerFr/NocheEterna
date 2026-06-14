package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class HookManager implements Listener {

    private final NocheEterna plugin;
    private NocheEternaExpansion placeholderExpansion;
    private ProtocolLibEffects protocolLibEffects;
    private WorldGuardIntegration worldGuard;
    private VaultIntegration vault;
    private BossBarManager bossBar;

    public HookManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void hookAll() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new NocheEternaExpansion(plugin);
            if (placeholderExpansion.register()) {
                plugin.getLogger().info("[Integration] PlaceholderAPI expansion registered.");
                plugin.getLogger().info("  Placeholders: %nocheeterna_level%, %nocheeterna_phase%,"
                        + " %nocheeterna_phase_name%, %nocheeterna_progress%, %nocheeterna_next_phase%");
            }
        }

        if (plugin.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            protocolLibEffects = new ProtocolLibEffects(plugin);
            if (protocolLibEffects.setup()) {
                plugin.getLogger().info("[Integration] ProtocolLib effects active.");
                plugin.getLogger().info("  Features: fake chat, fake disconnect, psychological horror");
            }
        }

        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = new WorldGuardIntegration(plugin);
            if (worldGuard.setup()) {
                plugin.getLogger().info("[Integration] WorldGuard safe zones active.");
                plugin.getLogger().info("  Use /rg flag <region> no-darkness allow to create safe zones.");
            }
        }

        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            vault = new VaultIntegration(plugin);
            if (vault.setup()) {
                plugin.getLogger().info("[Integration] Vault economy rewards active.");
            }
        }

        bossBar = new BossBarManager(plugin);
        bossBar.start();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("[Integration] All hooks registered.");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (bossBar != null) {
            bossBar.removePlayer(event.getPlayer());
        }
    }

    public BossBarManager getBossBar() {
        return bossBar;
    }
}
