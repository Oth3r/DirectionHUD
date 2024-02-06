package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class DHUDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                DHUD.reload(null);
            }
            return true;
        }
        Player player = Player.of(plr);
        DHUD.commandExecutor.logic(player, Helper.Command.quoteHandler(args));
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        int pos = args.length;
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return new ArrayList<>();
        }
        Player player = Player.of(plr);
        return DHUD.commandSuggester.logic(player,pos,Helper.Command.quoteHandler(args));
    }
}
