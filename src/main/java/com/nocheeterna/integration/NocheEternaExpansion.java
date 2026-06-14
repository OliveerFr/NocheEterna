package com.nocheeterna.integration;

import com.nocheeterna.NocheEterna;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class NocheEternaExpansion extends PlaceholderExpansion {

    private final NocheEterna plugin;

    public NocheEternaExpansion(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "nocheeterna";
    }

    @Override
    public String getAuthor() {
        String[] authors = plugin.getDescription().getAuthors().toArray(new String[0]);
        return authors.length > 0 ? authors[0] : "OliveerF";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        return switch (identifier) {
            case "level" -> String.format("%.1f",
                    plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId()));
            case "level_int" -> String.valueOf((int) Math.floor(
                    plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId())));
            case "phase" -> plugin.getDarkLevelManager().getPhase(player.getUniqueId());
            case "phase_name" -> plugin.getConfigManager().getPhaseName(
                    plugin.getDarkLevelManager().getPhase(player.getUniqueId()))
                    .replace("\u00a7", "&");
            case "phase_percent" -> String.valueOf(
                    (int) (plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId())
                            * 100.0 / 100.0));
            case "progress" -> {
                double level = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
                yield getProgressBar(level);
            }
            case "next_phase" -> {
                String phase = plugin.getDarkLevelManager().getPhase(player.getUniqueId());
                yield switch (phase) {
                    case "twilight" -> plugin.getConfigManager().getPhaseName("night")
                            .replace("\u00a7", "&");
                    case "night" -> plugin.getConfigManager().getPhaseName("nightmare")
                            .replace("\u00a7", "&");
                    case "nightmare" -> plugin.getConfigManager().getPhaseName("abyss")
                            .replace("\u00a7", "&");
                    default -> "\u00a77MAX";
                };
            }
            default -> null;
        };
    }

    private String getProgressBar(double level) {
        int filled = (int) (level / 10);
        if (filled > 10) filled = 10;
        if (filled < 0) filled = 0;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < filled / 2 + (filled % 2)) {
                bar.append("\u00a75\u2588");
            } else {
                bar.append("\u00a77\u2588");
            }
        }
        return bar.toString();
    }
}
