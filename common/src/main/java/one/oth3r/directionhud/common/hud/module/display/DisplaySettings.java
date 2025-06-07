package one.oth3r.directionhud.common.hud.module.display;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class DisplaySettings {
    @SerializedName("displays")
    private Map<String, String> displays;
    @SerializedName("assets")
    private Assets assets;

    /**
     * adds a display via the ID
     * @param displayID the ID to add the display under
     * @param display the display to add
     */
    public void addDisplay(String displayID, String display) {
        if (displays == null) displays = new HashMap<>();
        displays.put(displayID, display);
    }

    /**
     * sets the display list directly
     * @param display the list to set to
     */
    public void setDisplays(Map<String, String> display) {
        displays = display;
    }

    /**
     * gets the display from the provided display ID
     * @param displayID the ID of the display to get
     */
    public String getDisplay(String displayID) {
        return displays == null ? "" : displays.get(displayID);
    }

    /**
     * adds a singular asset to an asset group, if the group doesn't exist, it is created
     * @param groupID the group for the asset
     * @param assetID the id of the asset
     * @param asset the asset to add
     */
    public void addAsset(String groupID, String assetID, String asset) {
        if (assets == null) assets = new Assets();
        assets.addAsset(groupID, assetID, asset);
    }

    /**
     * adds an asset group to the display settings
     * @param groupID the id for the group
     * @param groupAssets the asset group to add
     */
    public void addAssetGroup(String groupID, AssetGroup groupAssets) {
        if (assets == null) assets = new Assets();
        assets.addAssets(groupID, groupAssets);
    }

    /**
     * overrides the assets with the provided replacement
     */
    public void setAssets(Assets assets) {
        this.assets = new Assets(assets);
    }

    /**
     * gets an asset group from a group id
     */
    public AssetGroup getAssetGroup(String groupID) {
        return assets == null ? null : assets.getAssets(groupID);
    }

    /**
     * gets a singular asset string from the group io and asset id
     */
    public String getAsset(String groupID, String assetID) {
        return assets == null ? "" : assets.getAsset(groupID, assetID);
    }

    /**
     * updates the ModuleDisplay maps with the new provided display. <br/>
     * will only update existing entries in the current maps, and if the replacement isnt null
     */
    public void updateDisplay(DisplaySettings display) {
        for (Map.Entry<String, String> displayEntry : displays.entrySet()) {
            String innerKey = displayEntry.getKey();
            String inputValue = display.getDisplay(innerKey);
            if (inputValue != null) {
                displays.put(innerKey, inputValue);
            }
        }

        if (assets != null && display.assets != null) {
            for (String groupID : assets.getGroupIDs()) {
                AssetGroup innerMap = assets.getAssets(groupID);
                AssetGroup inputValue = display.getAssetGroup(groupID);
                if (inputValue != null) {
                    for (Map.Entry<String, String> assetEntry : inputValue.entrySet()) {
                        String innerAssetID = assetEntry.getKey();
                        String innerAsset = assetEntry.getValue();
                        if (innerAsset != null) {
                            innerMap.put(innerAssetID, innerAsset);
                        }
                    }
                }
            }
        }
    }

    /**
     * the object for a singular module display asset group, its just a String HashMap but it makes reading code easier with this class
     */
    public static class AssetGroup extends HashMap<String, String> {
        public AssetGroup() {
            super();
        }

        public AssetGroup(Map<String, String> map) {
            super(map != null ? map : new HashMap<>());
        }
    }

    /**
     * The object for the list of asset groups per module
     */
    public static class Assets extends HashMap<String, AssetGroup> {
        public Assets() {
            super();
        }

        public Assets(Map<String, AssetGroup> map) {
            super(map != null ? map : new HashMap<>());
        }

        public void addAsset(String groupID, String assetID, String asset) {
            this.computeIfAbsent(groupID, k -> new AssetGroup()).put(assetID, asset);
        }

        public void addAssets(String groupID, AssetGroup groupAssets) {
            this.put(groupID, new AssetGroup(groupAssets));
        }

        public AssetGroup getAssets(String groupID) {
            return this.get(groupID);
        }

        public String getAsset(String groupID, String assetID) {
            AssetGroup group = this.get(groupID);
            return group == null ? "" : group.getOrDefault(assetID, "");
        }

        public Iterable<String> getGroupIDs() {
            return this.keySet();
        }

    }
}
