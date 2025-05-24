package one.oth3r.directionhud.common.template;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public interface CustomFile <T extends CustomFile<T>> {

    void reset();

    /**
     * saves the current instance to file
     */
    default void save() {
        if (!Files.exists(getFile().toPath())) {
            DirectionHUD.LOGGER.info(String.format("Creating new `%s`", getFile().getName()));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8)) {
            writer.write(Helper.getGson().toJson(this));
        }
        // if the file doesn't exist at this stage it would be because of a bad directory, try fileNotExist() to create the directory
        catch (NoSuchFileException ignored) {
            fileNotExist();
        }
        catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR SAVING '%s`: %s", getFile().getName(), e));
        }
    }

    /**
     * loads the file to the current instance using updateFromReader()
     */
    default void load() {
        File file = getFile();
        if (!Files.exists(getFile().toPath())) fileNotExist();

        // try reading the file
        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            updateFromReader(reader);
        }
        // rare, but if the file doesn't exist at this stage, try again
        catch (NoSuchFileException ignored) {
            fileNotExist();
        }
        // cant load for some reason
        catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR LOADING '%s': %s", file.getName(),e));
        }
        // save after loading
        save();
    }

    default void updateFromReader(BufferedReader reader) {
        // try to read the json
        T file;
        JsonElement json = JsonParser.parseReader(reader);
        try {
            file = Helper.getGson().fromJson(json, getFileClass());
        } catch (Exception e) {
            throw new NullPointerException();
        }

        // if the file couldn't be parsed, (null) try using the custom update method using the JsonElement on the current file
        // if not use the new file object that is loaded with the file data, and call update using that
        if (file == null) {
            this.update(json);
        } else {
            // update the instance
            file.update(json);
            // load the file to the current object
            copyFileData(file);
        }
    }

    /**
     * @return the class of the File
     */
    Class<T> getFileClass();

    /**
     * loads the data from the file object into the current object - DEEP COPY
     * @param newFile the file to take the properties from
     */
    void copyFileData(T newFile);

    /**
     * updates the file based on the version number of the current instance
     */
    void update(JsonElement json);

    /**
     * logic for the file not existing when loading, defaults to saving
     */
    default void fileNotExist() {
        // try to make the config directory
        try {
            Files.createDirectories(Paths.get(getDirectory()));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Failed to create config directory. Canceling all config loading...");
            return;
        }
        save();
    }

    /**
     * gets the file name - including the extension
     * @return ex. custom-file.json
     */
    String getFileName();

    String getDirectory();

    default File getFile() {
        return new File(getDirectory()+getFileName());
    }
}
