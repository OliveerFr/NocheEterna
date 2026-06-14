package com.nocheeterna.api;

import java.util.UUID;

public interface DarkLevelService {
    double getDarkLevel(UUID playerUuid);
    void setDarkLevel(UUID playerUuid, double level);
    void addDarkLevel(UUID playerUuid, double amount);
    void removeDarkLevel(UUID playerUuid, double amount);
    String getPhase(double level);
    String getPhase(UUID playerUuid);
    String getPhaseDisplayName(double level);
}
