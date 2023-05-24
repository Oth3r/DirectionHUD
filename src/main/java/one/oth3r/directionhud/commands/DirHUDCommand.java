package one.oth3r.directionhud.commands;

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
        if (args.length == 0) {
            DirHUD.UI(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("defaults")) {
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
        if (args[0].equalsIgnoreCase("reload") && player.hasPermission("TEMP")) { //todo
            DirHUD.reload(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length+1;
        if (!(sender instanceof Player player)) {
            return new ArrayList<>();
        }
        if (pos == 1) {
//            if (!DirectionHUD.server.isRemote()) return builder.suggest("defaults").buildFuture();
            if (player.hasPermission("TEMP")) { //todo
                suggester.add("reload");
                return suggester;
            }
        }
        return suggester;
    }
}
