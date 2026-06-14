package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkPhaseChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.Method;

public class VaultIntegration implements Listener {

    private final NocheEterna plugin;
    private Object economy;
    private Class<?> economyClass;
    private Method depositMethod;
    private Method currencyNameMethod;
    private boolean enabled = false;

    public VaultIntegration(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        try {
            economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            RegisteredServiceProvider<?> rsp = Bukkit.getServicesManager()
                    .getRegistration(economyClass);
            if (rsp == null) {
                plugin.getLogger().info("[Integration] Vault found but no economy plugin detected.");
                return false;
            }
            economy = rsp.getProvider();
            depositMethod = economyClass.getMethod("depositPlayer", OfflinePlayer.class, double.class);
            currencyNameMethod = economyClass.getMethod("currencyNamePlural");

            Bukkit.getPluginManager().registerEvents(this, plugin);
            enabled = true;
            plugin.getLogger().info("[Integration] Vault hooked (reflection).");
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("[Integration] Vault hook failed: " + e.getMessage());
            return false;
        }
    }

    @EventHandler
    public void onPhaseChange(DarkPhaseChangeEvent event) {
        if (!enabled || economy == null) return;

        String phase = event.getNewPhase();
        double reward = plugin.getConfig().getDouble("vault.rewards." + phase, 0.0);

        if (reward <= 0) return;

        Player player = Bukkit.getPlayer(event.getPlayerUuid());
        if (player == null || !player.isOnline()) return;

        try {
            depositMethod.invoke(economy, player, reward);
            String currency = (String) currencyNameMethod.invoke(economy);
            player.sendMessage("\u00a7a+" + String.format("%.2f", reward) + " "
                    + currency + " \u00a78| \u00a75Noche Eterna");
        } catch (Exception e) {
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().warning("[Vault] Failed to reward " + player.getName()
                        + ": " + e.getMessage());
            }
        }
    }
}
