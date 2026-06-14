package com.nocheeterna.api;

import com.nocheeterna.NocheEterna;

public class InternalAPI implements NocheEternaAPI {

    private final NocheEterna plugin;
    private final DarkLevelService darkLevelService;
    private final HorrorService horrorService;

    public InternalAPI(NocheEterna plugin,
                       DarkLevelService darkLevelService,
                       HorrorService horrorService) {
        this.plugin = plugin;
        this.darkLevelService = darkLevelService;
        this.horrorService = horrorService;
    }

    @Override
    public DarkLevelService getDarkLevelService() {
        return darkLevelService;
    }

    @Override
    public HorrorService getHorrorService() {
        return horrorService;
    }

    @Override
    public boolean isPluginEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public void reloadPlugin() {
        plugin.reloadConfig();
        plugin.getConfigManager().load();
    }
}
