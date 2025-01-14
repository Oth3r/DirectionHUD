package one.oth3r.directionhud.common.files;

import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Utl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Updater {
    public static class DimSettings {

        /**
         * runs the updater from the file reader and sets the loaded settings when finished, adding the missing dimensions
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         */
        public static void run(BufferedReader reader)
                throws NullPointerException {
            // try to read the json
            DimensionSettings dimensionSettings;
            try {
                dimensionSettings = Helper.getGson().fromJson(reader, DimensionSettings.class);
            } catch (Exception e) {
                throw new NullPointerException();
            }

            // throw null if the fileData is null or version is null
            if (dimensionSettings == null) throw new NullPointerException();

            // get the file version
            Double version = dimensionSettings.getVersion();

            // if there's no version, throw
            if (version == null) throw new NullPointerException();

            // update the default pData
            dimensionSettings = update(dimensionSettings);

            // add missing dimensions
            Utl.dim.addMissing(dimensionSettings);

            // set the DefaultPData
            Dimension.setDimensionSettings(dimensionSettings);
        }

        /**
         * updates the file
         */
        public static DimensionSettings update(DimensionSettings old) {
            DimensionSettings dimensionSettings = new DimensionSettings(old);
            return dimensionSettings;
        }
    }

    public static class Global {

        /**
         * runs the updater from the file reader and sets the loaded settings when finished, adding the missing dimensions
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         */
        public static void run(BufferedReader reader)
                throws NullPointerException {
            // try to read the json
            GlobalDest globalDest;
            try {
                globalDest = Helper.getGson().fromJson(reader, GlobalDest.class);
            } catch (Exception e) {
                throw new NullPointerException();
            }

            // check if reading the file failed
            if (globalDest == null) {
                // run the legacy updater
                globalDest = legacyUpdater();
                // if the updater failed, throw null
                if (globalDest == null) {
                    throw new NullPointerException();
                }
            }

            // get the file version
            Double version = globalDest.getVersion();

            // if there's no version, throw
            if (version == null) {
                throw new NullPointerException();
            }

            // update
            globalDest = update(globalDest);

            // set the global destinations
            FileData.setGlobalDestinations(globalDest);
        }

        /**
         * updates the file
         */
        public static GlobalDest update(GlobalDest old) {
            GlobalDest globalDest = new GlobalDest(old);
            return globalDest;
        }

        /**
         * update the old string based global destination to the new object based system using a buffered reader
         * @return null if couldn't update / not legacy
         */
        public static GlobalDest legacyUpdater() {
            // try to read the file
            try (BufferedReader reader = Files.newBufferedReader(GlobalDest.getFile().toPath(), StandardCharsets.UTF_8)){
                List<List<String>> legacy = Helper.getGson().fromJson(reader, new TypeToken<List<List<String>>>() {}.getType());
                // if the file still couldn't be read
                if (legacy == null) return null;

                ArrayList<Dest> updated = new ArrayList<>();
                // convert each entry to the new dest system
                legacy.forEach(entry -> {
                    // entry = name | Loc | color
                    Dest dest = new Dest(new Loc(true, entry.get(1)),entry.get(0),entry.get(2));
                    // if valid add to list
                    if (dest.hasDestRequirements()) updated.add(dest);
                });

                GlobalDest globalDest = new GlobalDest();
                globalDest.setDestinations(updated);

                // log the update
                DirectionHUD.LOGGER.info("Successfully loaded legacy global destinations & migrated to new system!");
                // return the updated global destination file
                return globalDest;
            } catch (IOException e) {
                // return null if unable
                return null;
            }
        }
    }
}
