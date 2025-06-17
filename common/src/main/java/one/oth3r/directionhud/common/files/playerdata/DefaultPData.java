package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.JsonElement;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.module.ModuleManager;
import one.oth3r.directionhud.common.template.CustomFile;
import org.jetbrains.annotations.NotNull;

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
        super.copyBaseFileData(newFile);
    }

    @Override
    public JsonElement updateJSON(JsonElement json) {
        // update
        return baseJSONUpdater(json, true);
    }

    /**
     * POST LOAD: after the JSON is loaded to this current instance, this method is called.
     */
    @Override
    public void updateFileInstance() {
        // run the module order fixer just in case
        ModuleManager.Order.fixOrder(this.hud.getModules(), true);
    }

    @Override
    public String getFileName() {
        return "default-playerdata.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.getData().getConfigDirectory();
    }
}
