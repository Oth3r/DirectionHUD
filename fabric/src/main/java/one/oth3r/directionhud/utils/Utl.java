package one.oth3r.directionhud.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
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
        return new CTxT(Component.translatable(key, args));
    }
    public static CTxT getTxTFromObj(Object obj) {
        CTxT txt = new CTxT();
        if (obj instanceof CTxT) txt.append(((CTxT) obj).b());
        else if (obj instanceof Component) txt.append((MutableComponent) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static List<DPlayer> getPlayers() {
        ArrayList<DPlayer> array = new ArrayList<>();
        for (ServerPlayer p : DirectionHUD.getData().getServer().getPlayerList().getPlayers())
            array.add(new DPlayer(p));
        return array;
    }

    /**
     * Gets the side of the block pos that the player is looking at. The block pos will be where a block *would* be placed.
     * @param serverPlayer the server player
     * @param range the maximum range to check for
     * @return the block pos where a block *would* be placed OR NULL if not found.
     */
    public static BlockPos getSideOfBlockPosPlayerIsLookingAt(ServerPlayer serverPlayer, double range) {
        // pos, adjusted to player eye level
        Vec3 rayStart = serverPlayer.position().add(0, serverPlayer.getEyeHeight(serverPlayer.getPose()), 0);
        // extend ray by the range
        Vec3 rayEnd = rayStart.add(serverPlayer.getLookAngle().scale(range));

        BlockHitResult hitResult = serverPlayer.level().clip(new ClipContext(rayStart, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty()));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getBlockPos().relative(hitResult.getDirection());
        }

        return null;
    }

    /**
     * gets the player's block interaction range
     * @param player the player to check
     */
    public static double getPlayerReach(Player player) {
        // use the BLOCK_INTERACTION_RANGE attribute if available
        if (player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE) != null) {
            return player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        }
        // fallback to 5
        return 5;
    }

    public static class CheckEnabled extends FeatureChecker {
        public CheckEnabled(DPlayer player) {
            super(player);
        }

        @Override
        public boolean globalEditing() {
            return super.globalEditing() && (player.getPlayer().permissions().hasPermission(Permissions.COMMANDS_ADMIN) || DirectionHUD.getData().isSingleplayer() ||
                    FileData.getConfig().getDestination().getGlobal().getPublicEditing()); // if public editing is enabled, allow global editing
        }

        @Override
        public boolean send() {
            return super.send() && DirectionHUD.getData().getServer().isPublished();
        }

        @Override
        public boolean track() {
            return super.track() && DirectionHUD.getData().getServer().isPublished();
        }

        @Override
        public boolean reload()
        {
            return player.getPlayer().permissions().hasPermission(Permissions.COMMANDS_ADMIN) || DirectionHUD.getData().isSingleplayer();
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
        public static String format(ResourceKey<Level> worldRegistryKey) {
            return worldRegistryKey.identifier().toString();
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
            for (ServerLevel world : DirectionHUD.getData().getServer().getAllLevels()) {
                String currentDIM = format(world.dimension());
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
        private static String getFormattedDim(ServerLevel world) {
            // get the path of the dimension, removes the "minecraft:"
            String formatted = world.dimension().identifier().getPath();
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
