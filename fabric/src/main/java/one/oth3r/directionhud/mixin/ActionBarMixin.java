package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.CTxT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class ActionBarMixin {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void sendMessage(Text message, boolean tinted, CallbackInfo info) {
        if (!config.actionBarChat) return;
        MinecraftClient client = MinecraftClient.getInstance();
        // get the actionbar's click event
        ClickEvent click = message.getStyle().getClickEvent();
        // create the message to send the player
        CTxT msg = CTxT.of(CUtl.lang("info.actionbar").append(" ").append(CTxT.of((MutableText) message).italic(true))).hEvent(CUtl.lang("info.actionbar_hover"));
        if (click == null || !click.getValue().equals("https://modrinth.com/mod/directionhud")) {
            if (message.getString().equals("")) return;
            if (client.player != null) {
                if (DirectionHUDClient.onSupportedServer && DirectionHUDClient.packetData.get("state").equals(true)) {
                    if (DirectionHUDClient.packetData.get("type").equals(config.HUDTypes.actionbar.toString()))
                        client.player.sendMessage(msg.b());
                }
            }
        }
    }
}