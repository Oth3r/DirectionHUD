package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalDest {
    public static List<String> dummyEntry = List.of("dummy entry");
    public static List<List<String>> dests = new ArrayList<>();
    public static File getFile() {
        return new File(DirectionHUD.DATA_DIR+"global-dest.json");
    }
    public static List<List<String>> fileToMap() {
        File file = getFile();
        if (!file.exists()) mapToFile();
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            dests = gson.fromJson(reader,new TypeToken<List<List<String>>>() {}.getType());
            // add dummy entry, for identifying between local and global tests, only in memory, never saved to file
            dests.add(dests.size(),dummyEntry);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
        return dests;
    }
    public static void mapToFile() {
        try (FileWriter writer = new FileWriter(getFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // remove the dummy entry when saving
            List<List<String>> fileDests = dests;
            fileDests.remove(dummyEntry);
            writer.write(gson.toJson(fileDests));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR SAVING GLOBAL DEST FILE - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }
}
