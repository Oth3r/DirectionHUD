package one.oth3r.directionhud.utils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.*;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.dimension.RatioEntry;
import one.oth3r.directionhud.common.template.FeatureChecker;
import one.oth3r.directionhud.common.utils.Helper.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

public class Utl {
    public static CTxT getTranslation(String key, Object... args) {
        return CTxT.of(Text.translatable(key, args));
    }
    public static CTxT getTxTFromObj(Object obj) {
        CTxT txt = CTxT.of("");
        if (obj instanceof CTxT) txt.append(((CTxT) obj).b());
        else if (obj instanceof Text) txt.append((Text) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList())
            array.add(new Player(p));
        return array;
    }

    public static class CheckEnabled extends FeatureChecker {
        public CheckEnabled(Player player) {
            super(player);
        }

        @Override
        public boolean globalEditing() {
            return super.globalEditing() && (player.getPlayer().hasPermissionLevel(2) || DirectionHUD.singleplayer);
        }

        @Override
        public boolean send() {
            return super.send() && DirectionHUD.server.isRemote();
        }

        @Override
        public boolean track() {
            return super.track() && DirectionHUD.server.isRemote();
        }

        @Override
        public boolean reload() {
            return player.getPlayer().hasPermissionLevel(2) || DirectionHUD.singleplayer;
        }
    }

    public static class dim {

        public static ArrayList<DimensionEntry> DEFAULT_DIMENSIONS = new ArrayList<>(Arrays.asList(
                new DimensionEntry("minecraft:overworld", "Overworld", "#55FF55",Dimension.OVERWORLD_TIME_ENTRY),
                new DimensionEntry("minecraft:the_nether", "Nether", "#e8342e", new Time(true)),
                new DimensionEntry("minecraft:the_end", "End", "#edffb0", new Time(true))
        ));

        public static ArrayList<RatioEntry> DEFAULT_RATIOS = new ArrayList<>(List.of(
                new RatioEntry(new Pair<>("minecraft:overworld", 1.0), new Pair<>("minecraft:the_nether", 8.0))
        ));

        /**
         * formats the un-formatted dimension received from the game
         * @param worldRegistryKey the worldRegistryKey
         * @return the formatted dimension string
         */
        public static String format(RegistryKey<World> worldRegistryKey) {
            return worldRegistryKey.getValue().toString();
        }

        /**
         * FABRIC ONLY
         * <p>
         * reverts back dimensions from "minecraft.dim" to "minecraft:dim" to be more vanilla like
         * @param oldDimension dimension to update
         * @return updated dimension
         */
        public static String updateLegacy(String oldDimension) {
            return oldDimension.replaceFirst("\\.",":");
        }

        /**
         * adds the dimensions that are loaded in game but aren't in the config yet
         */
        public static void addMissing(DimensionSettings dimensionSettings) {
            Random random = new Random();
            if (DirectionHUD.server == null) return;
            ArrayList<DimensionEntry> dimensions = dimensionSettings.getDimensions();
            //ADD MISSING DIMS TO MAP
            for (ServerWorld world : DirectionHUD.server.getWorlds()) {
                String currentDIM = format(world.getRegistryKey());
                // if already exist, continue
                if (dimensions.stream()
                        .anyMatch(dimension -> dimension.getId().equals(currentDIM)) ) continue;
                DimensionEntry entry = new DimensionEntry();
                // add the dimension name
                entry.setId(currentDIM);
                // add the formatted name
                entry.setName(getFormattedDim(world));
                //make a random color to spice things up
                entry.setColor(String.format("#%02x%02x%02x",
                        random.nextInt(100,256),random.nextInt(100,256),random.nextInt(100,256)));
                // add the entry
                dimensions.add(entry);
            }
        }

        /**
         * tries to generate a formatted name for the dimension
         * @param world the dimension world
         * @return formatted dimension string
         */
        @NotNull
        private static String getFormattedDim(ServerWorld world) {
            // get the path of the dimension, removes the "minecraft:"
            String formatted = world.getRegistryKey().getValue().getPath();
            // remove all "_" "the_nether" -> "the nether"
            formatted = formatted.replaceAll("_"," ");
            // remove 'the' "the nether" -> "nether"
            formatted = formatted.replaceFirst("the ","");
            // capitalize the key letter "nether" -> "Nether"
            formatted = formatted.substring(0,1).toUpperCase()+formatted.substring(1);
            return formatted;
        }
    }
}
