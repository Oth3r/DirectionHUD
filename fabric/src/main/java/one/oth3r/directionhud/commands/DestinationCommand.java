package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class DestinationCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dest")
                .requires((commandSource) -> commandSource.hasPermissionLevel(0))
                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                .then(CommandManager.argument("args", StringArgumentType.string())
                        .suggests((context, builder) -> getSuggestions(context,builder,1))
                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                        .then(CommandManager.argument("args", StringArgumentType.string())
                                .suggests((context, builder) -> getSuggestions(context,builder,2))
                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                .then(CommandManager.argument("args", StringArgumentType.string())
                                        .suggests((context, builder) -> getSuggestions(context,builder,3))
                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                .suggests((context, builder) -> getSuggestions(context,builder,4))
                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                .then(CommandManager.argument("args", StringArgumentType.string())
                                                        .suggests((context, builder) -> getSuggestions(context,builder,5))
                                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                                .suggests((context, builder) -> getSuggestions(context,builder,6))
                                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                .then(CommandManager.argument("args", StringArgumentType.string())
                                                                        .suggests((context, builder) -> getSuggestions(context,builder,7))
                                                                        .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                        .then(CommandManager.argument("args", StringArgumentType.string())
                                                                                .suggests((context, builder) -> getSuggestions(context,builder,8))
                                                                                .executes((context2) -> command(context2.getSource(), context2.getInput()))
                                                                                .executes((context2) -> command(context2.getSource(), context2.getInput())))))))))));
        dispatcher.register(CommandManager.literal("destination").redirect(dispatcher.getRoot().getChild("dest"))
                .executes((context2) -> command(context2.getSource(), context2.getInput())));
    }
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos) {
        Player player = Player.of(Objects.requireNonNull(context.getSource().getPlayer()));
        String[] args = context.getInput().split(" ");
        if (pos > args.length) return builder.buildFuture();
        args = Utl.trimStart(args,1);
        for (String s : Destination.commandSuggester.logic(player,pos, args)) builder.suggest(s);
        return builder.buildFuture();
    }
    private static int command(ServerCommandSource source, String arg) {
        ServerPlayerEntity spe = source.getPlayer();
        if (spe == null) return 1;
        Player player = Player.of(spe);
        String[] args;
        //trim all the arguments before the command
        List<String> keywords = Arrays.asList("dest", "destination");
        int index = Integer.MAX_VALUE;
        //finds the index for the words
        for (String keyword : keywords) {
            int keywordIndex = arg.indexOf(keyword);
            if (keywordIndex != -1 && keywordIndex < index) index = keywordIndex;
        }
        //trims the words before the text
        if (index != Integer.MAX_VALUE) arg = arg.substring(index).trim();
        args = arg.split(" ");
        if (args[0].equals("dest") || args[0].equals("destination"))
            args = arg.replaceFirst("(?i)dest(ination)?\\s+", "").split(" ");

        if (args[0].equalsIgnoreCase("dest") || args[0].equalsIgnoreCase("destination")) {
            args = new String[0];
        }
        Destination.commandExecutor.logic(player,args);
        return 1;
    }
}
