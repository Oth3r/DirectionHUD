package one.oth3r.directionhud.common.files.dimension;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.utils.Helper.*;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Dimension {
    /**
     * default time entry for the Overworld dimension
     */
    public static final DimensionEntry.Time OVERWORLD_TIME_ENTRY = new DimensionEntry.Time(
            new DimensionEntry.Time.Weather(
                    new DimensionEntry.Time.Weather.NightTicks(new DimensionEntry.Time.TimePair(12542,0),new DimensionEntry.Time.TimePair(12010,0),new DimensionEntry.Time.TimePair(0,24000)),
                    new DimensionEntry.Time.Weather.Icons(Assets.symbols.sun,Assets.symbols.moon,Assets.symbols.rain,Assets.symbols.thunder)));

    private static DimensionSettings dimensionSettings = new DimensionSettings();

    public static void setDimensionSettings(DimensionSettings dimensionSettings) {
        Dimension.dimensionSettings = new DimensionSettings(dimensionSettings);
    }

    public static DimensionSettings getDimensionSettings() {
        return dimensionSettings;
    }

    /**
     * returns all dimension ids
     * @return list with dimension ids
     */
    public static ArrayList<String> getAllIDs() {
        return dimensionSettings.getDimensions().stream()
                .map(DimensionEntry::getId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * returns the time settings for the dimension
     * @param id the dimension ID
     * @return time settings for the dimension
     */
    public static DimensionEntry.Time getTimeSettings(String id) {
        return dimensionSettings.getDimensions().stream()
                .filter(dimension -> dimension.getId().equals(id))
                .map(DimensionEntry::getTime)
                .findFirst().orElse(null);
    }

    /**
     * gets the formatted name of the dimension-id specified
     * @param id the dimension
     * @return the dimension's name
     */
    public static String getName(String id) {
        return dimensionSettings.getDimensions().stream()
                .filter(dimension -> dimension.getId().equals(id))
                .map(DimensionEntry::getName)
                .findFirst().orElse("unknown");
    }

    /**
     * gets the HEX color for the dimension-id specified
     * @param id the dimension
     * @return HEX color
     */
    public static String getColor(String id) {
        return dimensionSettings.getDimensions().stream()
                .filter(dimension -> dimension.getId().equals(id))
                .map(DimensionEntry::getColor)
                .findFirst().orElse("#FF0000");
    }

    /**
     * makes a one letter badge of the dimension, eg [O] for overworld
     * @param id the dimension
     * @return the badge
     */
    public static CTxT getBadge(String id) {
        // find the entry
        DimensionEntry dim = dimensionSettings.getDimensions().stream()
                .filter(dimension -> dimension.getId().equals(id))
                .findFirst().orElse(null);
        // if not found
        if (dim == null) return CTxT.of("X").btn(true).hEvent(CTxT.of("???"));
        // make the badge
        return CTxT.of(String.valueOf(dim.getName().charAt(0)).toUpperCase()).btn(true)
                .color(dim.getColor()).hEvent(CTxT.of(dim.getName()).color(dim.getColor()));
    }
    public static ArrayList<Pair<String, String>> getRatioPairs() {
        return dimensionSettings.getRatios().stream()
                .map(RatioEntry::getDimensionPair).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * checks if the two dimension's coordinates can be converted to each other
     * @param dimensionA dimension one
     * @param dimensionB dimension two
     * @return if the dimensions can be converted or not
     */
    public static boolean canConvert(String dimensionA, String dimensionB) {
        // both in same dim, cant convert
        if (dimensionA.equalsIgnoreCase(dimensionB)) return false;

        Pair<String, String> key = new Pair<>(dimensionA, dimensionB);
        // if the ratio exists (check both normal and flipped), it's true
        return getRatioPairs().contains(key) || getRatioPairs().contains(key.getFlipped());
    }

    /**
     * gets the ratio for 2 dimensions, flipped accordingly
     * @param dimensionFrom the from dimension
     * @param dimensionTo the to dimension
     * @return the ratio as a double
     */
    public static double getRatio(String dimensionFrom, String dimensionTo) {
        ArrayList<Pair<String,String>> pairs = getRatioPairs();
        Pair<String, String> dimensionPair = new Pair<>(dimensionFrom, dimensionTo), flippedPair = dimensionPair.getFlipped();
        if (pairs.contains(dimensionPair)) return dimensionSettings.getRatios().get(pairs.indexOf(dimensionPair)).getRatio();
            // flip the ratio if there's a flipped ratio
        else if (pairs.contains(flippedPair)) return 1 / dimensionSettings.getRatios().get(pairs.indexOf(flippedPair)).getRatio();
            // no ratio if no match
        else return 1.0;
    }

    /**
     * checks if the dimension is in the list of dimensions
     * @return if the dimension exists in the list or not
     */
    public static boolean checkValid(String id) {
        return dimensionSettings.getDimensions().stream()
                .anyMatch(dimension -> dimension.getId().equals(id));
    }

    // converts the old system of saving dimension settings to the new system
    public static ArrayList<RatioEntry> convertLegacyRatios(List<String> oldList) {
        ArrayList<RatioEntry> out = new ArrayList<>();
        for (String s : oldList) {
            String[] entries = s.split("\\|");
            if (entries.length != 2) continue;
            String[] entry1 = entries[0].split("="), entry2 = entries[1].split("=");
            RatioEntry entry = new RatioEntry();
            // update to the new fabric system of dimensions too
            entry.setDimension1(new Pair<>(Utl.dim.updateLegacy(entry1[0]),Double.parseDouble(entry1[1])));
            entry.setDimension2(new Pair<>(Utl.dim.updateLegacy(entry2[0]),Double.parseDouble(entry2[1])));
            out.add(entry);
        }
        return out;
    }

    public static ArrayList<DimensionEntry> convertLegacyDimensions(List<String> oldList) {
        ArrayList<DimensionEntry> list = new ArrayList<>();
        // for all config dimensions
        for (String entry : oldList) {
            String[] entries = entry.split("\\|");
            // if not correct length
            if (entries.length != 3) continue;

            // filling data, update to the new fabric system of dimensions
            DimensionEntry data = new DimensionEntry(
                    Utl.dim.updateLegacy(entries[0]), entries[1], entries[2],
                    // only enable time by default if DirectionHUD is a mod, because plugins have different times for different worlds.
                    new DimensionEntry.Time(DirectionHUD.isMod)
            );

            // if overworld add overworld time settings
            if (entries[0].contains("overworld")) {
                data.setTime(OVERWORLD_TIME_ENTRY);
            }

            // add the dimension data
            list.add(data);
        }
        return list;
    }
}
