package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.utils.Utl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DirHUDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                DirHUD.reload(null);
            }
            return true;
        }
        if (!player.hasPermission("directionhud.directionhud")) return true;
        if (args.length == 0) {
            DirHUD.UI(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("defaults") && player.hasPermission("directionhud.defaults")) {
            if (args.length == 1) {
                DirHUD.defaults(player);
            }
            if (args.length != 2) {
                return true;
            }
            if (args[1].equalsIgnoreCase("set")) {
                DirHUD.setDefaults(player);
            }
            if (args[1].equalsIgnoreCase("reset")) {
                DirHUD.resetDefaults(player);
            }
        }
        if (args[0].equalsIgnoreCase("reload") && player.hasPermission("directionhud.reload")) {
            DirHUD.reload(player);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length;
        if (!(sender instanceof Player player)) {
            return suggester;
        }
        if (!player.hasPermission("directionhud.directionhud")) return suggester;
        if (pos == 1) {
            if (player.hasPermission("directionhud.defaults")) suggester.add("defaults");
            if (player.hasPermission("directionhud.reload")) suggester.add("reload");
        }
        return Utl.formatSuggestions(suggester,args);
    }
}
