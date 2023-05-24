package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CUtl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HUDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (!config.HUDEditing) return true;

        if (args.length == 0) {
            HUD.UI(player, null);
            return true;
        }
        //MODULES
        if (args[0].equalsIgnoreCase("edit")) {
            //UI
            if (args.length == 1) {
                HUD.order.UI(player, null, null);
                return true;
            }
            //RESET
            if (args[1].equals("reset") && args.length == 2) {
                HUD.order.reset(player, true);
                return true;
            }
            //MOVE UP / DOWN CMD
            if (args[1].equals("move") && args.length == 4) {
                HUD.order.move(player, args[2], args[3], true);
                return true;
            }
            //TOGGLE
            if (args[1].equals("state") && args.length == 4) {
                HUD.order.toggle(player, args[2], Boolean.parseBoolean(args[3]), true);
                return true;
            }
            //SETTING
            if (args[1].equals("setting") && args.length == 4) {
                HUD.order.setting(player, args[2], args[3], true);
                return true;
            }
            return true;
        }
        //COLOR
        if (args[0].equalsIgnoreCase("color")) {
            if (args.length == 1) {
                HUD.color.UI(player, null);
                return true;
            }
            //COLOR
            if (args.length == 3 && args[1].equals("edt")) {
                if (args[2].equals("pri")) HUD.color.changeUI(player, "pri", null);
                if (args[2].equals("sec")) HUD.color.changeUI(player, "sec", null);
                return true;
            }
            //RESET
            if (args[1].equals("rset")) {
                if (args.length == 2) {
                    HUD.color.reset(player, null, true);
                    return true;
                }
                if (args.length == 3) {
                    HUD.color.reset(player, args[2], true);
                    return true;
                }
            }
            //SET COLOR
            if (args[1].equals("set") && args.length == 4) {
                HUD.color.setColor(player, args[2], args[3], true);
                return true;
            }
            if (args[1].equals("bold") && args.length == 4) {
                HUD.color.setBold(player, args[2], Boolean.parseBoolean(args[3]), true);
                return true;
            }
            if (args[1].equals("italics") && args.length == 4) {
                HUD.color.setItalics(player, args[2], Boolean.parseBoolean(args[3]), true);
                return true;
            }
            if (args[1].equals("rgb") && args.length == 4) {
                HUD.color.setRGB(player, args[2], Boolean.parseBoolean(args[3]), true);
                return true;
            }
            return true;
        }
        //TOGGLE
        if (args[0].equalsIgnoreCase("toggle")) {
            if (args.length == 1) {
                HUD.toggle(player, null, false);
                return true;
            }
            if (args.length != 2) return true;
            HUD.toggle(player, Boolean.parseBoolean(args[1]), true);
            return true;
        }
        player.spigot().sendMessage(CUtl.error(CUtl.lang("error.command")));
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length+1;
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }
        if (!config.HUDEditing) return suggester;
        if (pos == 1) {
            suggester.add("edit");
            suggester.add("color");
            suggester.add("toggle");
            return suggester;
        }
        if (pos > args.length) {
            return suggester;
        }
        if (pos == 4 && args[2].equals("set")) {
            suggester.add("ffffff");
            return suggester;
        }
        return suggester;
    }
}
