package com.nocheeterna;

import com.nocheeterna.commands.NocheCommand;
import com.nocheeterna.core.ConfigManager;
import com.nocheeterna.core.PlayerDataManager;
import com.nocheeterna.darklevel.DarkLevelListener;
import com.nocheeterna.darklevel.DarkLevelManager;
import com.nocheeterna.darklevel.DarkLevelTask;
import com.nocheeterna.horror.AmbientHorrorListener;
import com.nocheeterna.horror.AmbientHorrorManager;
import com.nocheeterna.integration.HookManager;
import com.nocheeterna.mobs.MobScaleManager;
import com.nocheeterna.network.NetworkSecurityManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NocheEterna extends JavaPlugin {

    private static NocheEterna instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private DarkLevelManager darkLevelManager;
    private AmbientHorrorManager ambientHorrorManager;
    private MobScaleManager mobScaleManager;
    private NetworkSecurityManager networkSecurityManager;
    private DarkLevelTask darkLevelTask;
    private HookManager hookManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.load();

        playerDataManager = new PlayerDataManager(this);
        playerDataManager.loadData();

        darkLevelManager = new DarkLevelManager(this);

        getServer().getPluginManager().registerEvents(
                new DarkLevelListener(this), this);

        networkSecurityManager = new NetworkSecurityManager(this);
        networkSecurityManager.register();

        if (configManager.isAmbientEnabled()) {
            getServer().getPluginManager().registerEvents(
                    new AmbientHorrorListener(this), this);
            ambientHorrorManager = new AmbientHorrorManager(this);
            ambientHorrorManager.start();
        }

        if (configManager.isMobsEnabled()) {
            mobScaleManager = new MobScaleManager(this);
            mobScaleManager.register();
        }

        darkLevelTask = new DarkLevelTask(this);
        int interval = configManager.getPassiveInterval();
        darkLevelTask.runTaskTimer(this, interval, interval);

        hookManager = new HookManager(this);
        hookManager.hookAll();

        getCommand("noche").setExecutor(new NocheCommand(this));

        getLogger().info("NocheEterna enabled - the darkness awaits.");
    }

    @Override
    public void onDisable() {
        if (darkLevelTask != null) darkLevelTask.cancel();
        if (ambientHorrorManager != null) ambientHorrorManager.stop();
        if (hookManager != null && hookManager.getBossBar() != null) hookManager.getBossBar().stop();
        if (playerDataManager != null) playerDataManager.saveData();

        getLogger().info("NocheEterna disabled.");
    }

    public static NocheEterna get() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public DarkLevelManager getDarkLevelManager() {
        return darkLevelManager;
    }

    public AmbientHorrorManager getAmbientHorrorManager() {
        return ambientHorrorManager;
    }

    public MobScaleManager getMobScaleManager() {
        return mobScaleManager;
    }

    public NetworkSecurityManager getNetworkSecurityManager() {
        return networkSecurityManager;
    }

    public HookManager getHookManager() {
        return hookManager;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        configManager.load();
    }
}
