package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.utils.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class HUDCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return true;
        }
        Player player = Player.of(plr);
        HUD.commandExecutor.logic(player,args);
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        int pos = args.length;
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return new ArrayList<>();
        }
        Player player = Player.of(plr);
        return HUD.commandSuggester.logic(player,pos,args);
    }
}
