package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WorldGuardIntegration implements Listener {

    private final NocheEterna plugin;
    private StateFlag noDarknessFlag;
    private boolean enabled = false;

    public WorldGuardIntegration(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return false;
        }
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            noDarknessFlag = new StateFlag("no-darkness", false);
            registry.register(noDarknessFlag);
            plugin.getLogger().info("[Integration] WorldGuard flag 'no-darkness' registered.");
        } catch (Exception e) {
            plugin.getLogger().warning("[Integration] Could not register WG flag: " + e.getMessage());
        }
        Bukkit.getPluginManager().registerEvents(this, plugin);
        enabled = true;
        plugin.getLogger().info("[Integration] WorldGuard hooked - safe zones available.");
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!enabled) return;
        if (event.getTo() == null) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;
        if (event.getPlayer().hasPermission("nocheeterna.bypass")) return;

        Player player = event.getPlayer();
        Location to = event.getTo();

        if (isSafeZone(to)) {
            plugin.getPlayerDataManager().removeDarkLevel(
                    player.getUniqueId(),
                    plugin.getConfigManager().getSurfaceDecay() * 2
            );
        }
    }

    public boolean isSafeZone(Location location) {
        if (!enabled || noDarknessFlag == null) return false;
        try {
            com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(location.getWorld());
            com.sk89q.worldedit.util.Location wgLoc = BukkitAdapter.adapt(location);
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            ApplicableRegionSet regions = query.getApplicableRegions(wgLoc);
            return regions.queryValue(null, noDarknessFlag) == StateFlag.State.ALLOW;
        } catch (Exception e) {
            return false;
        }
    }
}
