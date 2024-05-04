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
import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.playerdata.DefaultPData;
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
        addDrawableChild(ButtonWidget.builder(Text.literal(Config.getFile().getName()), button -> {
                    Util.getOperatingSystem().open(Config.getFile());
                })
                .dimensions(width / 2-100, 10, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button1")))
                .build());
        // dimension settings file
        addDrawableChild(ButtonWidget.builder(Text.literal(DimensionSettings.getFile().getName()), button -> {
                    Util.getOperatingSystem().open(DimensionSettings.getFile());
                })
                .dimensions(width / 2-100, 35, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button2")))
                .build());
        // default pData file
        addDrawableChild(
                ButtonWidget.builder(Text.literal(DefaultPData.getDefaultFile().getName()), button -> {
                    Util.getOperatingSystem().open(DefaultPData.getDefaultFile());
                })
                .dimensions(width / 2-100, 60, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of button3")))
                .build());
        // open folder button
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Open Folder"), button -> {
                            Util.getOperatingSystem().open(Paths.get(DirectionHUD.CONFIG_DIR).toUri());
                        })
                        .dimensions(width / 2-100, 85, 200, 20)
                        .tooltip(Tooltip.of(Text.literal("Tooltip of button3")))
                        .build());

        // open folder button
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Load Changes"), button -> {
                            Data.loadFiles(false);
                        })
                        .dimensions(width / 2 -98-2, height-30, 98, 20)
                        .tooltip(Tooltip.of(Text.literal("Tooltip of button3")))
                        .build());
        // open folder button
        addDrawableChild(
                ButtonWidget.builder(Text.literal("Back"), button -> {
                            client.setScreen(PARENT);
                            Data.loadFiles(false);
                        })
                        .dimensions(width / 2 +2, height-30, 98, 20)
                        .tooltip(Tooltip.of(Text.literal("Tooltip of button3")))
                        .build());
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(PARENT);
    }
}
