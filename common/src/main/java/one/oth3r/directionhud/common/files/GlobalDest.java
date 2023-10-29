package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class GlobalDest {
    public static List<List<String>> dests = new ArrayList<>();
    public static File getFile() {
        return new File(DirectionHUD.DATA_DIR+"global-dest.json");
    }
    public static void fileToMap() {
        File file = getFile();
        if (!file.exists()) mapToFile();
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            dests = gson.fromJson(reader,new TypeToken<List<List<String>>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void mapToFile() {
        try (FileWriter writer = new FileWriter(getFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(dests));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ops and LAN owners can add and edit the global destinations
    // enable via the config, commands only for adding and editing

    // Destination.saved.Dest setting things return booleans, and the error messages are more vague, only using the boolean to send a message or not
    // the same suggester could be used, just different hooks, very plain, as its for admins
}
