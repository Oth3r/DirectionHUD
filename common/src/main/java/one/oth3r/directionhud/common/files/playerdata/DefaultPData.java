package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.JsonElement;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.common.utils.Helper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DefaultPData extends BasePData implements CustomFile<DefaultPData> {

    public DefaultPData() {}

    public DefaultPData(DefaultPData defaults) {
        copyFileData(defaults);
    }

    @Override
    public void reset() {
        copyFileData(new DefaultPData());
    }

    @Override
    public @NotNull Class<DefaultPData> getFileClass() {
        return DefaultPData.class;
    }

    @Override
    public void copyFileData(DefaultPData newFile) {
        this.version = newFile.version;
        this.hud = new PDHud(newFile.hud);
        this.destination = new PDDestination();
        this.colorPresets = newFile.colorPresets.stream()
                .map(record -> new Helper.ColorPreset(record.name(), record.color()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void update(JsonElement json) {
        // update
        baseUpdater(json);
        // make sure the order and modules are valid
        Hud.modules.fixOrder(hud.getModules(),true);
    }

    @Override
    public String getFileName() {
        return "default-playerdata.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.CONFIG_DIR;
    }
}
