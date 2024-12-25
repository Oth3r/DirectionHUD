package one.oth3r.directionhud.common.template;

import net.fabricmc.loader.api.FabricLoader;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Helper;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface CustomFile <T extends CustomFile<T>> {

    void reset();

    /**
     * saves the current instance to file
     */
    default void save() {
        if (!getFile().exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new `%s`", getFile().getName()));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8)) {
            writer.write(Helper.getGson().toJson(this));
        } catch (Exception e) {
            DirectionHUD.LOGGER.error(String.format("ERROR SAVING '%s`: %s", getFile().getName(), e.getMessage()));
        }
    }

    /**
     * loads the file to the current instance using updateFromReader()
     */
    default void load() {
        File file = getFile();
        if (!file.exists()) fileNotExist();
        // try reading the file
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            updateFromReader(reader);
        } catch (Exception e) {
            DirectionHUD.LOGGER.error(String.format("ERROR LOADING '%s`: %s", file.getName(),e.getMessage()));
        }
    }

    default void updateFromReader(BufferedReader reader) {
        // try to read the json
        T file;
        try {
            file = Helper.getGson().fromJson(reader, getFileClass());
        } catch (Exception e) {
            throw new NullPointerException();
        }
        // throw null if the fileData is null or version is null
        if (file == null) throw new NullPointerException();

        // update the instance
        file.update();
        // load the file to the current object
        loadFileData(file);
    }

    @NotNull
    Class<T> getFileClass();

    /**
     * loads the data from the file object into the current object
     * @param newFile the file to take the properties from
     */
    void loadFileData(T newFile);

    /**
     * updates the file based on the version number of the current instance
     */
    void update();

    /**
     * logic for the file not existing when loading, defaults to saving
     */
    default void fileNotExist() {
        // try to make the config directory
        try {
            Files.createDirectories(Paths.get(getDirectory()));
        } catch (Exception e) {
            DirectionHUD.LOGGER.error("Failed to create config directory. Canceling all config loading...");
            return;
        }
        save();
    }

    String getFileName();

    default String getDirectory() {
        return FabricLoader.getInstance().getConfigDir().toFile()+"/";
    }

    default File getFile() {
        return new File(getDirectory()+getFileName());
    }
}
