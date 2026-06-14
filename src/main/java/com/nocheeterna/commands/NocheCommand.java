package com.nocheeterna.commands;

import com.nocheeterna.NocheEterna;
import com.nocheeterna.core.ConfigManager;
import com.nocheeterna.darklevel.DarkLevelManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NocheCommand implements CommandExecutor, TabCompleter {

    private final NocheEterna plugin;

    public NocheCommand(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("nocheeterna.admin")) {
                    sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                plugin.getConfigManager().load();
                sender.sendMessage(plugin.getConfigManager().getMessage("reload"));
            }
            case "level" -> handleLevel(sender, args);
            case "phase" -> handlePhase(sender);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void handleLevel(CommandSender sender, String[] args) {
        ConfigManager cfg = plugin.getConfigManager();

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console: /noche level <player>");
                return;
            }
            double level = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
            String phase = plugin.getDarkLevelManager().getPhase(level);
            String phaseDisplay = cfg.getPhaseName(phase);
            String msg = cfg.getMessage("level-command.self")
                    .replace("%d", String.format("%.1f", level))
                    .replace("%s", phaseDisplay);
            sender.sendMessage(msg);
            return;
        }

        if (!sender.hasPermission("nocheeterna.admin")) {
            sender.sendMessage(cfg.getMessage("no-permission"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(cfg.getPrefix() + "\u00a7cPlayer not found.");
            return;
        }

        if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
            try {
                double value = Double.parseDouble(args[3]);
                plugin.getDarkLevelManager().setDarkLevel(target.getUniqueId(), value);
                double level = plugin.getDarkLevelManager().getDarkLevel(target.getUniqueId());
                String phase = plugin.getDarkLevelManager().getPhase(level);
                String phaseDisplay = cfg.getPhaseName(phase);
                String msg = cfg.getMessage("level-command.set")
                        .replace("%s", target.getName())
                        .replace("%d", String.format("%.1f", level))
                        .replace("%s", phaseDisplay);
                sender.sendMessage(msg);
            } catch (NumberFormatException e) {
                sender.sendMessage(cfg.getPrefix() + "\u00a7cInvalid number.");
            }
            return;
        }

        double level = plugin.getDarkLevelManager().getDarkLevel(target.getUniqueId());
        String phase = plugin.getDarkLevelManager().getPhase(level);
        String phaseDisplay = cfg.getPhaseName(phase);
        String msg = cfg.getMessage("level-command.other")
                .replaceFirst("%s", target.getName())
                .replace("%d", String.format("%.1f", level))
                .replace("%s", phaseDisplay);
        sender.sendMessage(msg);
    }

    private void handlePhase(CommandSender sender) {
        ConfigManager cfg = plugin.getConfigManager();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return;
        }
        double level = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
        String phase = plugin.getDarkLevelManager().getPhase(level);
        String phaseDisplay = cfg.getPhaseName(phase);
        sender.sendMessage(cfg.getPrefix() + "\u00a77Current phase: " + phaseDisplay
                + " \u00a77(level " + String.format("%.1f", level) + "/100)");
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().getPrefix()
                + "\u00a77/noche <reload|level|phase>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "level", "phase"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("level")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("level")) {
            completions.add("set");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("level") && args[2].equalsIgnoreCase("set")) {
            for (int i = 0; i <= 100; i += 20) {
                completions.add(String.valueOf(i));
            }
        }
        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }
}
