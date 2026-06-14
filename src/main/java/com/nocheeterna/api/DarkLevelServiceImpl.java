package com.nocheeterna.api;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.events.LevelChangeEvent;

import java.util.UUID;

public class DarkLevelServiceImpl implements DarkLevelService {

    private final NocheEterna plugin;

    public DarkLevelServiceImpl(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public double getDarkLevel(UUID uuid) {
        return plugin.getPlayerDataManager().getDarkLevel(uuid);
    }

    @Override
    public void setDarkLevel(UUID uuid, double level) {
        double oldLevel = getDarkLevel(uuid);
        String oldPhase = getPhase(oldLevel);
        plugin.getPlayerDataManager().setDarkLevel(uuid, level);
        double newLevel = getDarkLevel(uuid);
        String newPhase = getPhase(newLevel);

        plugin.getServer().getPluginManager().callEvent(
                new LevelChangeEvent(uuid, oldLevel, newLevel, LevelChangeEvent.ChangeCause.COMMAND));

        if (!oldPhase.equals(newPhase)) {
            plugin.getServer().getPluginManager().callEvent(
                    new com.nocheeterna.darklevel.DarkPhaseChangeEvent(uuid, oldPhase, newPhase));
        }
    }

    @Override
    public void addDarkLevel(UUID uuid, double amount) {
        setDarkLevel(uuid, getDarkLevel(uuid) + amount);
    }

    @Override
    public void removeDarkLevel(UUID uuid, double amount) {
        setDarkLevel(uuid, getDarkLevel(uuid) - amount);
    }

    @Override
    public String getPhase(double level) {
        if (level >= 81) return "abyss";
        if (level >= 51) return "nightmare";
        if (level >= 21) return "night";
        return "twilight";
    }

    @Override
    public String getPhase(UUID uuid) {
        return getPhase(getDarkLevel(uuid));
    }

    @Override
    public String getPhaseDisplayName(double level) {
        return plugin.getConfigManager().getPhaseName(getPhase(level));
    }
}
