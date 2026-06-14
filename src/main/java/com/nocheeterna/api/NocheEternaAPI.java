package com.nocheeterna.api;

public interface NocheEternaAPI {
    DarkLevelService getDarkLevelService();
    HorrorService getHorrorService();
    boolean isPluginEnabled();
    void reloadPlugin();
}
