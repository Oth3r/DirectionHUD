package one.oth3r.directionhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ModCommands {
    private enum Command {
        DIRECTIONHUD(new String[]{"dhud", "directionhud"}),
        DESTINATION(new String[]{"dest", "destination"}),
        HUD(new String[]{"hud"});

        private final String[] variations;

        Command(String[] variations) {
            this.variations = variations;
        }

        public String[] getVariations() {
            return variations;
        }
    }

    /**
     * registers fabric dhud commands
     */
    public static class Register {
        public static void destination(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(CommandManager.literal("dest")
                    .requires((commandSource) -> commandSource.hasPermissionLevel(0))
                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                    .then(CommandManager.argument("args", StringArgumentType.string())
                            .suggests((context, builder) -> getSuggestions(context,builder,1, Command.DESTINATION))
                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                            .then(CommandManager.argument("args", StringArgumentType.string())
                                    .suggests((context, builder) -> getSuggestions(context,builder,2, Command.DESTINATION))
                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                            .suggests((context, builder) -> getSuggestions(context,builder,3, Command.DESTINATION))
                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                    .suggests((context, builder) -> getSuggestions(context,builder,4, Command.DESTINATION))
                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                            .suggests((context, builder) -> getSuggestions(context,builder,5, Command.DESTINATION))
                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                    .suggests((context, builder) -> getSuggestions(context,builder,6, Command.DESTINATION))
                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                                            .suggests((context, builder) -> getSuggestions(context,builder,7, Command.DESTINATION))
                                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                                    .suggests((context, builder) -> getSuggestions(context,builder,8, Command.DESTINATION))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION)))))))))));
            dispatcher.register(CommandManager.literal("destination").redirect(dispatcher.getRoot().getChild("dest"))
                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DESTINATION)));
        }

        public static void hud(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(CommandManager.literal("hud")
                    .requires((commandSource) -> commandSource.hasPermissionLevel(0))
                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                    .then(CommandManager.argument("args", StringArgumentType.string())
                            .suggests((context, builder) -> getSuggestions(context,builder,1, Command.HUD))
                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                            .then(CommandManager.argument("args", StringArgumentType.string())
                                    .suggests((context, builder) -> getSuggestions(context,builder,2, Command.HUD))
                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                            .suggests((context, builder) -> getSuggestions(context,builder,3, Command.HUD))
                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                    .suggests((context, builder) -> getSuggestions(context,builder,4, Command.HUD))
                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                            .suggests((context, builder) -> getSuggestions(context,builder,5, Command.HUD))
                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                    .suggests((context, builder) -> getSuggestions(context,builder,6, Command.HUD))
                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                                            .suggests((context, builder) -> getSuggestions(context,builder,7, Command.HUD))
                                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                                    .suggests((context, builder) -> getSuggestions(context,builder,8, Command.HUD))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.HUD)))))))))));
        }

        public static void directionhud(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(CommandManager.literal("dhud")
                    .requires((commandSource) -> commandSource.hasPermissionLevel(0))
                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                    .then(CommandManager.argument("args", StringArgumentType.string())
                            .suggests((context, builder) -> getSuggestions(context,builder,1, Command.DIRECTIONHUD))
                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                            .then(CommandManager.argument("args", StringArgumentType.string())
                                    .suggests((context, builder) -> getSuggestions(context,builder,2, Command.DIRECTIONHUD))
                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                            .suggests((context, builder) -> getSuggestions(context,builder,3, Command.DIRECTIONHUD))
                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                    .suggests((context, builder) -> getSuggestions(context,builder,4, Command.DIRECTIONHUD))
                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                            .suggests((context, builder) -> getSuggestions(context,builder,5, Command.DIRECTIONHUD))
                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                    .suggests((context, builder) -> getSuggestions(context,builder,6, Command.DIRECTIONHUD))
                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                                                    .then(CommandManager.argument("args", StringArgumentType.string())
                                                                            .suggests((context, builder) -> getSuggestions(context,builder,7, Command.DIRECTIONHUD))
                                                                            .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                                                            .then(CommandManager.argument("args", StringArgumentType.string())
                                                                                    .suggests((context, builder) -> getSuggestions(context,builder,8, Command.DIRECTIONHUD))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD))
                                                                                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD)))))))))));
            dispatcher.register(CommandManager.literal("directionhud").redirect(dispatcher.getRoot().getChild("dhud"))
                    .executes((context2) -> execute(context2.getSource(), context2.getInput(), Command.DIRECTIONHUD)));
        }
    }

    /**
     * executes the command
     */
    private static int execute(ServerCommandSource source, String arg, Command command) {
        // get the player and make sure they're not null
        ServerPlayerEntity spe = source.getPlayer();
        if (spe == null) return 1;
        Player player = new Player(spe);

        // get the fixed args
        String[] args = Helper.Command.removeTo(arg.split(" "), command.getVariations());
        // fix arguments with quotes
        args = Helper.Command.quoteHandler(args);

        // use the right execution logic based on the command
        switch (command) {
            case DIRECTIONHUD -> DHud.CMDExecutor(player,args);
            case DESTINATION -> Destination.commandExecutor.logic(player,args);
            case HUD -> Hud.CMDExecutor(player,args);
        }

        // return success
        return 1;
    }

    /**
     * gets suggestions for a command
     */
    private static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder, int pos, Command command) {
        // get the executor
        Player player = new Player(context.getSource().getPlayer());
        // get the current command args
        String[] args = context.getInput().split(" ");
        // if the suggestion query is greater than the amount of args in the current command, return empty
        if (pos > args.length) return builder.buildFuture();

        // remove exes args, eg from the execute command
        args = Helper.Command.removeTo(args,command.getVariations());
        // fix arguments with quotes
        args = Helper.Command.quoteHandler(args);

        // get the suggestions from the arguments
        ArrayList<String> suggestions = new ArrayList<>();
        switch (command) {
            case DIRECTIONHUD -> suggestions = DHud.CMDSuggester(player,pos,args);
            case DESTINATION -> suggestions = Destination.commandSuggester.logic(player,pos,args);
            case HUD -> suggestions = Hud.CMDSuggester(player,pos,args);
        }

        // add the suggestions and build the suggester
        suggestions.forEach(builder::suggest);
        return builder.buildFuture();
    }

}
