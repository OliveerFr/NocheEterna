package com.nocheeterna.api;

import org.bukkit.entity.Player;

public interface HorrorService {
    void applyPhaseEffects(Player player, String phase);
    void playPhaseTransition(Player player, String oldPhase, String newPhase);
    void applyFog(Player player, String phase);
}
