package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Loc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class GlobalDest {
    private static final Loc versionEntry = new Loc(1,null,0,null,"1.0",null);

    /**
     * file destinations, including the versionEntry (use for detecting if a list is a global or not)
     */
    private static List<Loc> fileDestinations = new ArrayList<>();
    public static List<Loc> getFileDestinations() {
        // detached
        return new ArrayList<>(fileDestinations);
    }

    /**
     * destination list for editing or iterating
     */
    private static List<Loc> destinations = new ArrayList<>();
    public static List<Loc> getDestinations() {
        return new ArrayList<>(destinations);
    }
    public static void setDestinations(List<Loc> destinations) {
        GlobalDest.destinations = new ArrayList<>(destinations);
        // make sure the fileDestinations are also synced when setting the destination list
        List<Loc> list = getDestinations();
        list.add(0,versionEntry);
        fileDestinations = list;
    }
    public static void clear() {
        destinations.clear();
        fileDestinations.clear();
    }
    public static File getFile() {
        return new File(DirectionHUD.DATA_DIR+"global-dest.json");
    }
    public static List<Loc> load() {
        File file = getFile();
        // if no file, load it
        if (!file.exists()) mapToFile();
        try (FileReader reader = new FileReader(file)) {
            // get the data
            ArrayList<String> data = new Gson().fromJson(reader,new TypeToken<ArrayList<String>>(){}.getType());
            ArrayList<Loc> out = new ArrayList<>();
            data.forEach(entry -> out.add(new Loc(entry)));

            // check for version (first entry)
            if (!out.get(0).hasDestRequirements()) {
                // get the version number from the version entry
                float version = Float.parseFloat(out.get(0).getName());
                // remove all invalid entries
                out.removeIf(entry -> !entry.hasDestRequirements());
            }

            // copy the list and set to destinations
            destinations = new ArrayList<>(out);
            // add the current version to the end for the fileDestinations
            out.add(0,versionEntry);
            // set to fileDest list
            fileDestinations = out;
            // save in case of changes
            mapToFile();
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
        return destinations;
    }
    public static void mapToFile() {
        try (FileWriter writer = new FileWriter(getFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(fileDestinations));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR SAVING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }

    /**
     * loads the legacy global dest file, converting everything to the new system
     */
    public static void loadLegacy() {
        File file = getFile();
        // if no file, cancel
        if (!file.exists()) return;
        try (FileReader reader = new FileReader(file)) {
            List<List<String>> legacy = new Gson().fromJson(reader, new TypeToken<List<List<String>>>() {}.getType());
            ArrayList<Loc> out = new ArrayList<>();
            // convert each entry to the new dest system
            legacy.forEach(entry -> {
                // entry = name | Loc | color
                Loc dest = new Loc(true, entry.get(1));
                dest.setName(entry.get(0));
                dest.setColor(entry.get(2));
                // if valid add to list
                if (dest.hasDestRequirements()) out.add(dest);
            });
            // add the current version to the end
            out.add(versionEntry);
            destinations = out;
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }
}
