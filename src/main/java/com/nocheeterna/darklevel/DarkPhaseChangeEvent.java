package com.nocheeterna.darklevel;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class DarkPhaseChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UUID playerUuid;
    private final String oldPhase;
    private final String newPhase;

    public DarkPhaseChangeEvent(UUID playerUuid, String oldPhase, String newPhase) {
        this.playerUuid = playerUuid;
        this.oldPhase = oldPhase;
        this.newPhase = newPhase;
    }

    public UUID getPlayerUuid() { return playerUuid; }
    public String getOldPhase() { return oldPhase; }
    public String getNewPhase() { return newPhase; }

    @Override
    public HandlerList getHandlers() { return handlers; }

    public static HandlerList getHandlerList() { return handlers; }
}
