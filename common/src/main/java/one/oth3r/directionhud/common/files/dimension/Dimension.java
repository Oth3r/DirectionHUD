package one.oth3r.directionhud.common.files.dimension;

import one.oth3r.directionhud.common.utils.Helper.*;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Dimension {

    private static DimensionSettings dimensionSettings = new DimensionSettings();

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

    /**
     * loads the dimensionSettings from the file, adds any missing dimensions, and saves
     */
    public static void load() {
        dimensionSettings = DimensionSettings.get();
        Utl.dim.addMissing();
        save();
    }

    /**
     * saves the current dimensionSettings to file
     */
    public static void save() {
        DimensionSettings.put(dimensionSettings);
    }

    // converts the old system of saving dimension settings to the new system
    public static ArrayList<RatioEntry> convertLegacyRatios(List<String> oldList) {
        ArrayList<RatioEntry> out = new ArrayList<>();
        for (String s : oldList) {
            String[] entries = s.split("\\|");
            if (entries.length != 2) continue;
            String[] entry1 = entries[0].split("="), entry2 = entries[1].split("=");
            RatioEntry entry = new RatioEntry();
            entry.setDimension1(new Pair<>(entry1[0],Double.parseDouble(entry1[1])));
            entry.setDimension2(new Pair<>(entry2[0],Double.parseDouble(entry2[1])));
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
            // filling data
            DimensionEntry data = new DimensionEntry();
            data.setId(entries[0]);
            data.setName(entries[1]);
            data.setColor(entries[2]);
            // add the dimension
            list.add(data);
        }
        return list;
    }
}