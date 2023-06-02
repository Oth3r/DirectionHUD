package one.oth3r.directionhud.spigot.commands;

import one.oth3r.directionhud.common.DirHUD;
import one.oth3r.directionhud.spigot.utils.Player;
import one.oth3r.directionhud.spigot.utils.Utl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DirHUDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                DirHUD.reload(null);
            }
            return true;
        }
        Player player = Player.of(plr);
        assert player != null;
        if (!Utl.checkEnabled.dirhud(player)) return true;
        if (args.length == 0) {
            DirHUD.UI(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("defaults") && Utl.checkEnabled.defaults(player)) {
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
        if (args[0].equalsIgnoreCase("reload") && Utl.checkEnabled.reload(player)) {
            DirHUD.reload(player);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length;
        if (!(sender instanceof org.bukkit.entity.Player plr)) return suggester;
        Player player = Player.of(plr);
        assert player != null;
        if (!Utl.checkEnabled.dirhud(player)) return suggester;
        if (pos == 1) {
            if (Utl.checkEnabled.defaults(player)) suggester.add("defaults");
            if (Utl.checkEnabled.reload(player)) suggester.add("reload");
        }
        return Utl.formatSuggestions(suggester,args);
    }
}
