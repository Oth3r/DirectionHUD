package one.oth3r.directionhud.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
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
        addRenderableWidget(Button.builder(Component.literal(FileData.getConfig().getFileName()), button -> {
                    Util.getPlatform().openUri(FileData.getConfig().getFileName());
                })
                .bounds(width / 2-100, 10, 200, 20)
                .tooltip(Tooltip.create(LANG.get("tooltip.file").b()))
                .build());
        // dimension settings file
        addRenderableWidget(Button.builder(Component.literal(Dimension.getDimensionSettings().getFileName()), button -> {
                    Util.getPlatform().openFile(Dimension.getDimensionSettings().getFile());
                })
                .bounds(width / 2-100, 35, 200, 20)
                .tooltip(Tooltip.create(LANG.get("tooltip.file").b()))
                .build());
        // default pData file
        addRenderableWidget(
                Button.builder(Component.literal(PlayerData.getDefaults().getFileName()), button -> {
                    Util.getPlatform().openFile(PlayerData.getDefaults().getFile());
                })
                .bounds(width / 2-100, 60, 200, 20)
                .tooltip(Tooltip.create(LANG.get("tooltip.file").b()))
                .build());
        // open folder button
        addRenderableWidget(
                Button.builder(LANG.btn("folder").b(), button -> {
                            Util.getPlatform().openUri(Paths.get(DirectionHUD.getData().getConfigDirectory()).toUri());
                        })
                        .bounds(width / 2-100, 85, 200, 20)
                        .tooltip(Tooltip.create(LANG.get("tooltip.folder").b()))
                        .build());

        // open folder button
        addRenderableWidget(
                Button.builder(LANG.btn("save").b(), button -> {
                            minecraft.setScreen(PARENT);
                            FileData.loadFiles();
                        })
                        .bounds(width / 2-100, height-30, 200, 20)
                        .tooltip(Tooltip.create(LANG.get("tooltip.save").b()))
                        .build());
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(PARENT);
    }
}
