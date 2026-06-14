package com.nocheeterna.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class LevelChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID playerUuid;
    private final double oldLevel;
    private final double newLevel;
    private final double delta;
    private final ChangeCause cause;

    public enum ChangeCause {
        PASSIVE_GAIN,
        NIGHT,
        CAVE,
        DEATH,
        DAMAGE,
        SURFACE_DECAY,
        SAFE_ZONE,
        COMMAND,
        OTHER
    }

    public LevelChangeEvent(UUID playerUuid, double oldLevel, double newLevel, ChangeCause cause) {
        this.playerUuid = playerUuid;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.delta = newLevel - oldLevel;
        this.cause = cause;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public double getOldLevel() { return oldLevel; }
    public double getNewLevel() { return newLevel; }
    public double getDelta() { return delta; }
    public ChangeCause getCause() { return cause; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
