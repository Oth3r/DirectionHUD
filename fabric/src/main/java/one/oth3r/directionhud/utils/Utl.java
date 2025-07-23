package one.oth3r.directionhud.utils;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.*;
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
        else if (obj instanceof Text) txt.append((MutableText) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (ServerPlayerEntity p : DirectionHUD.getData().getServer().getPlayerManager().getPlayerList())
            array.add(new Player(p));
        return array;
    }

    /**
     * Gets the side of the block pos that the player is looking at. The block pos will be where a block *would* be placed.
     * @param serverPlayer the server player
     * @param range the maximum range to check for
     * @return the block pos where a block *would* be placed OR NULL if not found.
     */
    public static BlockPos getSideOfBlockPosPlayerIsLookingAt(ServerPlayerEntity serverPlayer, double range) {
        // pos, adjusted to player eye level
        Vec3d rayStart = serverPlayer.getPos().add(0, serverPlayer.getEyeHeight(serverPlayer.getPose()), 0);
        // extend ray by the range
        Vec3d rayEnd = rayStart.add(serverPlayer.getRotationVector().multiply(range));

        BlockHitResult hitResult = serverPlayer.getWorld().raycast(new RaycastContext(rayStart, rayEnd, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getBlockPos().offset(hitResult.getSide());
        }

        return null;
    }

    /**
     * gets the player's block interaction range
     * @param player the player to check
     */
    public static double getPlayerReach(PlayerEntity player) {
        // use the BLOCK_INTERACTION_RANGE attribute if available
        if (player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE) != null) {
            return player.getAttributeValue(EntityAttributes.BLOCK_INTERACTION_RANGE);
        }
        // fallback to 5
        return 5;
    }

    public static class CheckEnabled extends FeatureChecker {
        public CheckEnabled(Player player) {
            super(player);
        }

        @Override
        public boolean globalEditing() {
            return super.globalEditing() && (player.getPlayer().hasPermissionLevel(2) || DirectionHUD.getData().isSingleplayer() ||
                    FileData.getConfig().getDestination().getGlobal().getPublicEditing()); // if public editing is enabled, allow global editing
        }

        @Override
        public boolean send() {
            return super.send() && DirectionHUD.getData().getServer().isRemote();
        }

        @Override
        public boolean track() {
            return super.track() && DirectionHUD.getData().getServer().isRemote();
        }

        @Override
        public boolean reload() {
            return player.getPlayer().hasPermissionLevel(2) || DirectionHUD.getData().isSingleplayer();
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
        public static void addMissing(ArrayList<DimensionEntry> dimensions) {
            Random random = new Random();
            if (DirectionHUD.getData().getServer() == null) return;
            //ADD MISSING DIMS TO MAP
            for (ServerWorld world : DirectionHUD.getData().getServer().getWorlds()) {
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
