package com.nocheeterna.mobs;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.darklevel.DarkLevelManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class MobScaleManager implements Listener {

    private final NocheEterna plugin;
    private final NamespacedKey scaledKey;

    public MobScaleManager(NocheEterna plugin) {
        this.plugin = plugin;
        this.scaledKey = new NamespacedKey(plugin, "scaled");
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMobTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        if (isExcluded(monster)) return;
        if (monster.getPersistentDataContainer().has(scaledKey, PersistentDataType.BYTE)) return;

        String phase = plugin.getDarkLevelManager().getPhase(player.getUniqueId());
        scaleMob(monster, phase);
        monster.getPersistentDataContainer().set(scaledKey, PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) return;
        if (isExcluded(monster)) return;

        int cap = plugin.getConfigManager().getEntitiesPerChunkCap();
        int count = 0;
        for (org.bukkit.entity.Entity e : event.getLocation().getChunk().getEntities()) {
            if (e instanceof Monster && !e.isDead()) count++;
        }
        if (count >= cap) {
            event.setCancelled(true);
        }
    }

    private void scaleMob(Monster monster, String phase) {
        double hpMult = plugin.getConfigManager().getPhaseDouble("mobs.hp-multiplier", phase, 1.0);
        double dmgMult = plugin.getConfigManager().getPhaseDouble("mobs.damage-multiplier", phase, 1.0);
        double spdMult = plugin.getConfigManager().getPhaseDouble("mobs.speed-multiplier", phase, 1.0);

        AttributeInstance hp = monster.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (hp != null) {
            double base = hp.getBaseValue();
            hp.setBaseValue(base * hpMult);
            monster.setHealth(hp.getBaseValue());
        }

        AttributeInstance speed = monster.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(speed.getBaseValue() * spdMult);
        }

        AttributeInstance attackDamage = monster.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.setBaseValue(attackDamage.getBaseValue() * dmgMult);
        }

        if (phase.equals("nightmare") || phase.equals("abyss")) {
            if (plugin.getConfig().getBoolean("mobs.glow-eyes.enabled", true)) {
                monster.setGlowing(true);
            }
        }
    }

    private boolean isExcluded(LivingEntity entity) {
        java.util.List<String> excluded = plugin.getConfig().getStringList("mobs.excluded-mobs");
        return excluded.contains(entity.getType().name());
    }
}
