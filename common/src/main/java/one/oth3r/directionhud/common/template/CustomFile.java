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
        // todo port to OtterLib
        // try to read the json
        T file;
        JsonElement json = JsonParser.parseReader(reader);
        // update the json element so it can be read properly
        json = this.updateJSON(json);
        file = Helper.getGson().fromJson(json, getFileClass());

        // if the file couldn't be parsed, (null) throw an exception,
        // otherwise copy over the read file to the current file & run the post update func
        if (file == null) {
            throw new NullPointerException();
        } else {
            copyFileData(file);
            updateFileInstance();
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
     * override to update the raw JsonElement before being parsed to the file object. Any changes to *this* file object will be DISCARDED.
     * @return the updated JsonElement
     */
    default JsonElement updateJSON(JsonElement json) {
        return json;
    }

    /**
     * POST LOAD: after the JSON is loaded to this current instance, this method is called.
     */
    void updateFileInstance();

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
