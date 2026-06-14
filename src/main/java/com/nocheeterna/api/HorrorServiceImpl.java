package com.nocheeterna.api;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.ux.PhaseTransitionSequence;
import org.bukkit.entity.Player;

public class HorrorServiceImpl implements HorrorService {

    private final NocheEterna plugin;

    public HorrorServiceImpl(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public void applyPhaseEffects(Player player, String phase) {
        if (plugin.getAmbientHorrorManager() != null) {
            if (plugin.getConfigManager().isFogEnabled()) {
                plugin.getAmbientHorrorManager().applyFogEffect(player, phase);
            }
            if (plugin.getConfigManager().isSoundsEnabled()) {
                plugin.getAmbientHorrorManager().playPhaseAmbientSound(player, phase);
            }
        }
    }

    @Override
    public void playPhaseTransition(Player player, String oldPhase, String newPhase) {
        PhaseTransitionSequence.play(plugin, player, oldPhase, newPhase);
    }

    @Override
    public void applyFog(Player player, String phase) {
        if (plugin.getAmbientHorrorManager() != null) {
            plugin.getAmbientHorrorManager().applyFogEffect(player, phase);
        }
    }
}
