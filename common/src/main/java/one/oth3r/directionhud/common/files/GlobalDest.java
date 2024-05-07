package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Loc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class GlobalDest {
    private static final Float VERSION = 1.0f;

    private static final Dest VERSION_ENTRY = new Dest(1,null,0,null,VERSION.toString(),null);

    /**
     * destination list for editing or iterating
     */
    private static ArrayList<Dest> destinations = new ArrayList<>();

    public static ArrayList<Dest> getDestinations() {
        return new ArrayList<>(destinations);
    }

    public static void setDestinations(List<Dest> destinations) {
        GlobalDest.destinations = new ArrayList<>(destinations);
    }

    public static void clear() {
        destinations.clear();
    }

    public static File getFile() {
        return new File(DirectionHUD.DATA_DIR+"global-dest.json");
    }

    public static void load() {
        File file = getFile();
        // if no file, load it
        if (!file.exists()) mapToFile();
        try (FileReader reader = new FileReader(file)) {
            // get the data
            ArrayList<Dest> data = new Gson().fromJson(reader,new TypeToken<ArrayList<Dest>>(){}.getType());

            float version = VERSION;
            // check for version (first entry)
            if (!data.isEmpty() && !data.get(0).hasDestRequirements() &&
                    data.get(0).getName() != null) {
                // get the version number from the version entry
                version = Float.parseFloat(data.get(0).getName());
            }
            // remove all invalid entries even if version
            data.removeIf(entry -> !entry.hasDestRequirements());
            // run updater here

            // set the data to destinations list
            destinations = data;
            // save in case of changes
            mapToFile();
        } catch (Exception e) {
            System.out.println("Global Destinations: cant load properly, loading legacy destinations...");
            loadLegacy();
        }
    }

    public static void mapToFile() {
        try (FileWriter writer = new FileWriter(getFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ArrayList<Dest> out = new ArrayList<>(destinations);
            out.add(0, VERSION_ENTRY);
            writer.write(gson.toJson(out));
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
            ArrayList<Dest> out = new ArrayList<>();
            // convert each entry to the new dest system
            legacy.forEach(entry -> {
                // entry = name | Loc | color
                Dest dest = new Dest(new Loc(true, entry.get(1)),entry.get(0),entry.get(2));
                // if valid add to list
                if (dest.hasDestRequirements()) out.add(dest);
            });
            destinations = out;
            // save the changes
            mapToFile();
            System.out.println("Global Destinations: successfully loaded legacy destinations & migrated to new systems!");
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
    }
}
