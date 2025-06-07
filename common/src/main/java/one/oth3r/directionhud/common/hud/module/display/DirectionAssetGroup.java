package one.oth3r.directionhud.common.hud.module.display;

public class DirectionAssetGroup {
    public static final String NORTH_EAST = "north_east";
    public static final String NORTH = "north";
    public static final String NORTH_WEST = "north_west";
    public static final String WEST = "west";
    public static final String SOUTH_WEST = "south_west";
    public static final String SOUTH = "south";
    public static final String SOUTH_EAST = "south_east";
    public static final String EAST = "east";

    /**
     * creates an asset group for all the directions using the provided strings for each direction
     */
    public static DisplaySettings.AssetGroup create(
            String northEast, String north, String northWest,
            String west, String southWest, String south,
            String southEast, String east) {
        DisplaySettings.AssetGroup assetGroup = new DisplaySettings.AssetGroup();
        assetGroup.put(NORTH_EAST, northEast);
        assetGroup.put(NORTH, north);
        assetGroup.put(NORTH_WEST, northWest);
        assetGroup.put(WEST, west);
        assetGroup.put(SOUTH_WEST, southWest);
        assetGroup.put(SOUTH, south);
        assetGroup.put(SOUTH_EAST, southEast);
        assetGroup.put(EAST, east);
        return assetGroup;
    }
}
