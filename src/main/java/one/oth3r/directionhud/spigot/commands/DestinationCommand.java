package one.oth3r.directionhud.spigot.commands;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.spigot.utils.Player;
import one.oth3r.directionhud.spigot.utils.CUtl;
import one.oth3r.directionhud.spigot.utils.Utl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class DestinationCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return true;
        }
        Player player = Player.of(plr);
        assert player != null;
        if (!Utl.checkEnabled.destination(player)) return true;
        if (args.length == 0) {
            Destination.UI(player);
            return true;
        }
        String type = args[0].toLowerCase();
        String[] trimmedArgs = Utl.trimStart(args, 1);
        switch (type) {
            case "set" -> Destination.commandExecutor.setCMD(player, trimmedArgs);
            case "clear" -> Destination.clear(player, null);
            case "saved" -> Destination.commandExecutor.savedCMD(player, trimmedArgs);
            case "add" -> Destination.commandExecutor.addCMD(player, trimmedArgs);
            case "remove" -> Destination.commandExecutor.removeCMD(player, trimmedArgs);
            case "lastdeath" -> Destination.commandExecutor.lastdeathCMD(player, trimmedArgs);
            case "settings" -> Destination.commandExecutor.settingsCMD(player, trimmedArgs);
            case "send" -> Destination.commandExecutor.sendCMD(player, trimmedArgs);
            case "track" -> Destination.commandExecutor.trackCMD(player, trimmedArgs);
            default -> player.sendMessage(CUtl.error(CUtl.lang("error.command")));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        int pos = args.length;
        if (!(sender instanceof org.bukkit.entity.Player plr)) {
            return suggester;
        }
        Player player = Player.of(plr);
        assert player != null;
        if (!Utl.checkEnabled.destination(player)) return suggester;
        if (pos == 1) {
            suggester.addAll(Destination.commandSuggester.base(player));
        }
        if (args.length >= 1) {
            //SAVED
            if (args[0].equalsIgnoreCase("saved")) {
                suggester.addAll(Destination.commandSuggester.savedCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                suggester.addAll(Destination.commandSuggester.addCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("settings")) {
                suggester.addAll(Destination.commandSuggester.settingsCMD(pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("set")) {
                suggester.addAll(Destination.commandSuggester.setCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("send")) {
                suggester.addAll(Destination.commandSuggester.sendCMD(player,pos-2,Utl.trimStart(args,1)));
            }
            if (args[0].equalsIgnoreCase("track")) {
                suggester.addAll(Destination.commandSuggester.trackCMD(player,pos-2));
            }
        }
        return Utl.formatSuggestions(suggester,args);
    }
}