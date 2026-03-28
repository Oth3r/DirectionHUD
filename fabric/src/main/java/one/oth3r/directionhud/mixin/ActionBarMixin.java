package one.oth3r.directionhud.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.ModData;
import one.oth3r.directionhud.utils.DPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(Gui.class)
public class ActionBarMixin {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V")
    private void sendMessage(Component message, boolean tinted, CallbackInfo info) {
        // no point in doing anything if the message is empty
        if (message.getString().isEmpty()) return;
        Minecraft client = Minecraft.getInstance();
        // get the actionbar's click event (otterlib CTxT compiling always has an empty base, so get the first sibling for the encoding)
        ClickEvent click = message.getSiblings().getFirst().getStyle().getClickEvent();
        // if the click event has the Modrinth link, it's a directionhud actionbar
        if (click == null ||
                !(click.action().getSerializedName().equals("open_url") && ((ClickEvent.OpenUrl) click).uri().equals(URI.create("https://modrinth.com/mod/directionhud")))) {
            if (client.player == null) return;

            DPlayer player = new DPlayer(client.player,true);
            ModData modData = DirectionHUD.getData();
            // if on supported server and hud is on AND hud type is actionbar
            PDHud hud = player.getPData().getHud();
            if (modData.isOnSupportedServer() &&
                    (boolean) hud.getSetting(Hud.Setting.state) &&
                    hud.getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString())) {
                modData.getActionBarOverride().setOverride(new CTxT((MutableComponent) message));
            }
        }
    }
}