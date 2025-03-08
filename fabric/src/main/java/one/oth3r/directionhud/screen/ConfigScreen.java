package one.oth3r.directionhud.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.Config;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.playerdata.DefaultPData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.utils.Lang;

import java.nio.file.Paths;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private final Screen PARENT;

    public static final Lang LANG = new Lang("config.");

    public ConfigScreen(Screen parent) {
        super(LANG.ui("main").b());
        this.PARENT = parent;
    }

    @Override
    protected void init() {
        super.init();
        // config file
        addDrawableChild(ButtonWidget.builder(Text.literal(FileData.getConfig().getFileName()), button -> {
                    Util.getOperatingSystem().open(FileData.getConfig().getFileName());
                })
                .dimensions(width / 2-100, 10, 200, 20)
                .tooltip(Tooltip.of(LANG.get("tooltip.file").b()))
                .build());
        // dimension settings file
        addDrawableChild(ButtonWidget.builder(Text.literal(Dimension.getDimensionSettings().getFileName()), button -> {
                    Util.getOperatingSystem().open(Dimension.getDimensionSettings().getFile());
                })
                .dimensions(width / 2-100, 35, 200, 20)
                .tooltip(Tooltip.of(LANG.get("tooltip.file").b()))
                .build());
        // default pData file
        addDrawableChild(
                ButtonWidget.builder(Text.literal(PlayerData.getDefaults().getFileName()), button -> {
                    Util.getOperatingSystem().open(PlayerData.getDefaults().getFile());
                })
                .dimensions(width / 2-100, 60, 200, 20)
                .tooltip(Tooltip.of(LANG.get("tooltip.file").b()))
                .build());
        // open folder button
        addDrawableChild(
                ButtonWidget.builder(LANG.btn("folder").b(), button -> {
                            Util.getOperatingSystem().open(Paths.get(DirectionHUD.getData().getConfigDirectory()).toUri());
                        })
                        .dimensions(width / 2-100, 85, 200, 20)
                        .tooltip(Tooltip.of(LANG.get("tooltip.folder").b()))
                        .build());

        // open folder button
        addDrawableChild(
                ButtonWidget.builder(LANG.btn("save").b(), button -> {
                            client.setScreen(PARENT);
                            FileData.loadFiles();
                        })
                        .dimensions(width / 2-100, height-30, 200, 20)
                        .tooltip(Tooltip.of(LANG.get("tooltip.save").b()))
                        .build());
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(PARENT);
    }
}
