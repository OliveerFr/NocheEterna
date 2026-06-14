package com.nocheeterna.core;

import com.nocheeterna.NocheEterna;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final NocheEterna plugin;
    private boolean enabled;
    private boolean debug;

    private double passiveGain;
    private int passiveInterval;
    private double nightGain;
    private double caveGain;
    private double deathGain;
    private double damageGain;
    private double surfaceDecay;
    private int saveInterval;

    private boolean ambientEnabled;
    private boolean fogEnabled;
    private boolean soundsEnabled;
    private boolean skyDarkenEnabled;
    private boolean particlesEnabled;

    private boolean mobsEnabled;

    private String serverName;

    private boolean bungeeCordEnabled;
    private List<String> allowedProxies;
    private boolean enforceIpForward;
    private int maxCommands;
    private int rateWindowSeconds;
    private int entitiesPerChunkCap;

    public ConfigManager(NocheEterna plugin) {
        this.plugin = plugin;
    }

    public void load() {
        FileConfiguration c = plugin.getConfig();
        enabled = c.getBoolean("enabled", true);
        debug = c.getBoolean("debug", false);

        passiveGain = c.getDouble("dark-level.passive-gain.amount", 0.05);
        passiveInterval = c.getInt("dark-level.passive-gain.interval-ticks", 600);
        nightGain = c.getDouble("dark-level.night-gain", 0.15);
        caveGain = c.getDouble("dark-level.cave-gain", 0.25);
        deathGain = c.getDouble("dark-level.death-gain", 5.0);
        damageGain = c.getDouble("dark-level.damage-gain", 0.1);
        surfaceDecay = c.getDouble("dark-level.surface-decay", 0.02);
        saveInterval = c.getInt("dark-level.save-interval", 6000);

        ambientEnabled = c.getBoolean("ambient.fog.enabled", true)
                || c.getBoolean("ambient.sounds.enabled", true)
                || c.getBoolean("ambient.sky-darken.enabled", true)
                || c.getBoolean("ambient.particles.enabled", true);
        fogEnabled = c.getBoolean("ambient.fog.enabled", true);
        soundsEnabled = c.getBoolean("ambient.sounds.enabled", true);
        skyDarkenEnabled = c.getBoolean("ambient.sky-darken.enabled", true);
        particlesEnabled = c.getBoolean("ambient.particles.enabled", true);

        mobsEnabled = c.getBoolean("mobs.enabled", true);

        serverName = c.getString("server-name", "survival");

        bungeeCordEnabled = c.getBoolean("network.bungeecord.enabled", true);
        allowedProxies = c.getStringList("network.bungeecord.allowed-proxies");
        enforceIpForward = c.getBoolean("network.bungeecord.enforce-ip-forward", true);
        maxCommands = c.getInt("network.rate-limit.max-commands", 10);
        rateWindowSeconds = c.getInt("network.rate-limit.window-seconds", 10);
        entitiesPerChunkCap = c.getInt("network.anti-exploit.entities-per-chunk-cap", 8);
    }

    public boolean isEnabled() { return enabled; }
    public boolean isDebug() { return debug; }
    public double getPassiveGain() { return passiveGain; }
    public int getPassiveInterval() { return passiveInterval; }
    public double getNightGain() { return nightGain; }
    public double getCaveGain() { return caveGain; }
    public double getDeathGain() { return deathGain; }
    public double getDamageGain() { return damageGain; }
    public double getSurfaceDecay() { return surfaceDecay; }
    public int getSaveInterval() { return saveInterval; }
    public boolean isAmbientEnabled() { return ambientEnabled; }
    public boolean isFogEnabled() { return fogEnabled; }
    public boolean isSoundsEnabled() { return soundsEnabled; }
    public boolean isSkyDarkenEnabled() { return skyDarkenEnabled; }
    public boolean isParticlesEnabled() { return particlesEnabled; }
    public boolean isMobsEnabled() { return mobsEnabled; }
    public String getServerName() { return serverName; }
    public boolean isBungeeCordEnabled() { return bungeeCordEnabled; }
    public List<String> getAllowedProxies() { return allowedProxies; }
    public boolean isEnforceIpForward() { return enforceIpForward; }
    public int getMaxCommands() { return maxCommands; }
    public int getRateWindowSeconds() { return rateWindowSeconds; }
    public int getEntitiesPerChunkCap() { return entitiesPerChunkCap; }

    public String getPhaseName(String phaseKey) {
        FileConfiguration c = plugin.getConfig();
        return c.getString("messages.phase-names." + phaseKey, phaseKey);
    }

    public double getPhaseDouble(String path, String phase, double def) {
        return plugin.getConfig().getDouble(path + "." + phase, def);
    }

    public String getPrefix() {
        FileConfiguration c = plugin.getConfig();
        return c.getString("messages.prefix", "&8[&5Noche Eterna&8] ")
                .replace("&", "\u00a7");
    }

    public String getMessage(String key) {
        FileConfiguration c = plugin.getConfig();
        String raw = c.getString("messages." + key, "");
        return getPrefix() + raw.replace("&", "\u00a7");
    }
}
