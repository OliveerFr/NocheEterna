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

public class NocheCommand implements CommandExecutor, TabCompleter {

    private final NocheEterna plugin;

    public NocheCommand(NocheEterna plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> showHelp(sender);
            case "reload" -> handleReload(sender);
            case "level" -> handleLevel(sender, args);
            case "phase" -> handlePhase(sender);
            case "top" -> handleTop(sender);
            case "reset" -> handleReset(sender, args);
            default -> showHelp(sender);
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        String p = plugin.getConfigManager().getPrefix();
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        sender.sendMessage("\u00a75\u2726  Noche Eterna \u00a77Comandos");
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        sender.sendMessage("\u00a77  /noche \u00a78- \u00a77Muestra esta ayuda");
        sender.sendMessage("\u00a77  /noche level \u00a78- \u00a77Tu nivel de oscuridad");
        sender.sendMessage("\u00a77  /noche phase \u00a78- \u00a77Tu fase actual");
        sender.sendMessage("\u00a77  /noche top \u00a78- \u00a77Top 5 jugadores mas oscuros");
        if (sender.hasPermission("nocheeterna.admin")) {
            sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
            sender.sendMessage("\u00a7c  /noche level <player> [set <value>] \u00a78- \u00a77Admin");
            sender.sendMessage("\u00a7c  /noche reset <player> \u00a78- \u00a77Resetear nivel a 0");
            sender.sendMessage("\u00a7c  /noche reload \u00a78- \u00a77Recargar config");
        }
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("nocheeterna.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        plugin.reloadConfig();
        plugin.getConfigManager().load();
        sender.sendMessage(plugin.getConfigManager().getMessage("reload"));
    }

    private void handleLevel(CommandSender sender, String[] args) {
        ConfigManager cfg = plugin.getConfigManager();

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("\u00a7cConsole: /noche level <player>");
                return;
            }
            showLevelToPlayer(sender, player);
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
            handleSetLevel(sender, target, args[3]);
            return;
        }

        showLevelToPlayer(sender, target);
    }

    private void showLevelToPlayer(CommandSender sender, Player target) {
        ConfigManager cfg = plugin.getConfigManager();
        double level = plugin.getDarkLevelManager().getDarkLevel(target.getUniqueId());
        String phase = plugin.getDarkLevelManager().getPhase(level);
        String phaseName = cfg.getPhaseName(phase);

        String bar = buildProgressBar(level);

        if (sender == target) {
            sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
            sender.sendMessage("\u00a75\u2726  Tu nivel de oscuridad");
            sender.sendMessage("    " + bar + " \u00a75" + String.format("%.1f", level)
                    + "\u00a78/\u00a77100");
            sender.sendMessage("    \u00a78Fase: " + phaseName);
            sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        } else {
            String msg = cfg.getMessage("level-command.other")
                    .replaceFirst("%s", target.getName())
                    .replace("%d", String.format("%.1f", level))
                    .replace("%s", phaseName);
            sender.sendMessage(msg);
        }
    }

    private void handleSetLevel(CommandSender sender, Player target, String valueStr) {
        try {
            double value = Double.parseDouble(valueStr);
            plugin.getDarkLevelManager().setDarkLevel(target.getUniqueId(), value);
            double level = plugin.getDarkLevelManager().getDarkLevel(target.getUniqueId());
            String phase = plugin.getDarkLevelManager().getPhase(level);
            String phaseName = plugin.getConfigManager().getPhaseName(phase);
            String msg = plugin.getConfigManager().getMessage("level-command.set")
                    .replace("%s", target.getName())
                    .replace("%d", String.format("%.1f", level))
                    .replace("%s", phaseName);
            sender.sendMessage(msg);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getConfigManager().getPrefix()
                    + "\u00a7cInvalid number.");
        }
    }

    private void handlePhase(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("\u00a7cOnly players can use this.");
            return;
        }
        ConfigManager cfg = plugin.getConfigManager();
        double level = plugin.getDarkLevelManager().getDarkLevel(player.getUniqueId());
        String phase = plugin.getDarkLevelManager().getPhase(level);
        String phaseName = cfg.getPhaseName(phase);

        String desc = switch (phase) {
            case "twilight" -> "\u00a7eLa oscuridad apenas comienza...";
            case "night" -> "\u00a79Los mobs son mas fuertes. Ten cuidado.";
            case "nightmare" -> "\u00a75La realidad se distorsiona. No confies en tus ojos.";
            case "abyss" -> "\u00a74\u00a7lEL ABISMO TE MIRA FIJO. NO HAY SALIDA.";
            default -> "";
        };

        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        sender.sendMessage("\u00a75\u2726  Fase actual: " + phaseName);
        sender.sendMessage("    " + desc);
        sender.sendMessage("    \u00a77Nivel: \u00a75" + String.format("%.1f", level)
                + "\u00a78/\u00a77100");
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void handleTop(CommandSender sender) {
        List<Player> sorted = new ArrayList<>(Bukkit.getOnlinePlayers());
        sorted.sort((a, b) -> Double.compare(
                plugin.getDarkLevelManager().getDarkLevel(b.getUniqueId()),
                plugin.getDarkLevelManager().getDarkLevel(a.getUniqueId())));

        int top = Math.min(5, sorted.size());
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        sender.sendMessage("\u00a75\u2726  Top " + top + " mas oscuros online");
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
        for (int i = 0; i < top; i++) {
            Player p = sorted.get(i);
            double level = plugin.getDarkLevelManager().getDarkLevel(p.getUniqueId());
            String phase = plugin.getDarkLevelManager().getPhase(level);
            String phaseName = plugin.getConfigManager().getPhaseName(phase);
            sender.sendMessage("\u00a77  " + (i + 1) + ". \u00a7f" + p.getName()
                    + " \u00a78- \u00a75" + String.format("%.1f", level)
                    + "\u00a78/100 " + phaseName);
        }
        sender.sendMessage("\u00a78\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500");
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("nocheeterna.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getConfigManager().getPrefix()
                    + "\u00a7cUsage: /noche reset <player>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getPrefix()
                    + "\u00a7cPlayer not found.");
            return;
        }
        plugin.getDarkLevelManager().setDarkLevel(target.getUniqueId(), 0);
        sender.sendMessage(plugin.getConfigManager().getPrefix()
                + "\u00a77Reset " + target.getName() + "'s dark level to 0.");
    }

    private String buildProgressBar(double level) {
        int filled = (int) (level / 5);
        if (filled > 20) filled = 20;
        if (filled < 0) filled = 0;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            if (i < filled) {
                bar.append("\u00a75\u2502");
            } else {
                bar.append("\u00a78\u2502");
            }
        }
        return bar.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("help", "level", "phase", "top", "reload", "reset"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("level")
                || args[0].equalsIgnoreCase("reset"))) {
            if (sender.hasPermission("nocheeterna.admin")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("level")) {
            if (sender.hasPermission("nocheeterna.admin")) {
                completions.add("set");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("level")
                && args[2].equalsIgnoreCase("set")) {
            if (sender.hasPermission("nocheeterna.admin")) {
                completions.addAll(Arrays.asList("0", "20", "50", "80", "100"));
            }
        }
        String input = args[args.length - 1].toLowerCase();
        completions.removeIf(s -> !s.toLowerCase().startsWith(input));
        return completions;
    }
}
