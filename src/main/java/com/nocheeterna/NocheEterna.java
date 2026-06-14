package com.nocheeterna;

import com.nocheeterna.api.InternalAPI;
import com.nocheeterna.api.NocheEternaAPI;
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
import com.nocheeterna.network.NetworkSyncManager;
import com.nocheeterna.network.NetworkJoinListener;
import com.nocheeterna.ux.ActionBarFeedback;
import com.nocheeterna.ux.PlayerWelcome;
import org.bukkit.plugin.java.JavaPlugin;

public final class NocheEterna extends JavaPlugin {

    private static NocheEterna instance;
    private static NocheEternaAPI api;

    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private DarkLevelManager darkLevelManager;
    private AmbientHorrorManager ambientHorrorManager;
    private MobScaleManager mobScaleManager;
    private NetworkSecurityManager networkSecurityManager;
    private NetworkSyncManager networkSyncManager;
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

        networkSyncManager = new NetworkSyncManager(this);
        networkSyncManager.register();
        getServer().getPluginManager().registerEvents(
                new NetworkJoinListener(this, networkSyncManager), this);

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
        darkLevelTask.checkSafeZone();
        int interval = configManager.getPassiveInterval();
        darkLevelTask.runTaskTimer(this, interval, interval);

        getServer().getPluginManager().registerEvents(
                new PlayerWelcome(this), this);

        ActionBarFeedback actionBar = new ActionBarFeedback(this);
        actionBar.start();

        hookManager = new HookManager(this);
        hookManager.hookAll();

        getCommand("noche").setExecutor(new NocheCommand(this));

        api = new InternalAPI(this, darkLevelManager,
                new com.nocheeterna.api.HorrorServiceImpl(this));

        getLogger().info("NocheEterna enabled - the darkness awaits.");
    }

    @Override
    public void onDisable() {
        if (darkLevelTask != null) darkLevelTask.cancel();
        if (ambientHorrorManager != null) ambientHorrorManager.stop();
        if (hookManager != null && hookManager.getBossBar() != null) hookManager.getBossBar().stop();
        if (networkSyncManager != null) networkSyncManager.unregister();
        if (playerDataManager != null) playerDataManager.saveData();
        instance = null;
        api = null;

        getLogger().info("NocheEterna disabled.");
    }

    public static NocheEterna get() {
        return instance;
    }

    public static NocheEternaAPI getAPI() {
        return api;
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

    public NetworkSyncManager getNetworkSyncManager() {
        return networkSyncManager;
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
