package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DHUDCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("dhud")
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
        dispatcher.register(CommandManager.literal("directionhud").redirect(dispatcher.getRoot().getChild("dhud"))
                .executes((context2) -> command(context2.getSource(), context2.getInput())));
    }
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos) {
        Player player = Player.of(Objects.requireNonNull(context.getSource().getPlayer()));
        String[] args = context.getInput().split(" ");
        if (pos > args.length) return builder.buildFuture();
        args = Helper.trimStart(args,1);
        for (String s : DHUD.CMDSuggester(player,pos,Helper.Command.quoteHandler(args))) builder.suggest(s);
        return builder.buildFuture();
    }
    private static int command(ServerCommandSource source, String arg) {
        ServerPlayerEntity spe = source.getPlayer();
        String[] args;
        //trim all the arguments before the command
        List<String> keywords = Arrays.asList("dhud", "directionhud");
        int index = Integer.MAX_VALUE;
        //finds the index for the words
        for (String keyword : keywords) {
            int keywordIndex = arg.indexOf(keyword);
            if (keywordIndex != -1 && keywordIndex < index) index = keywordIndex;
        }
        //trims the words before the text
        if (index != Integer.MAX_VALUE) arg = arg.substring(index).trim();
        args = arg.split(" ");
        if (args[0].equals("dhud") || args[0].equals("directionhud"))
            args = arg.replaceFirst("(?i)d(irection)?hud ", "").split(" ");
        if (spe == null) {
            if (args[0].equalsIgnoreCase("reload") && DirectionHUD.server.isRemote())
                DHUD.reload(null);
            return 1;
        }
        Player player = Player.of(spe);
        if (args[0].equalsIgnoreCase("dhud") || args[0].equalsIgnoreCase("directionhud"))
            args = new String[0];
        DHUD.CMDExecutor(player,Helper.Command.quoteHandler(args));
        return 1;
    }
}
