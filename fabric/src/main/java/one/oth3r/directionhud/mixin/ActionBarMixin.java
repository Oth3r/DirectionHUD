package one.oth3r.directionhud.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public class ActionBarMixin {
    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void sendMessage(Text message, boolean tinted, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClickEvent click = message.getStyle().getClickEvent();
        CTxT msg = CTxT.of(CUtl.lang("info.actionbar").append(" ").append(CTxT.of((MutableText) message).italic(true))).hEvent(CUtl.lang("info.actionbar_hover"));
        if (click == null || !click.getValue().equals("https://modrinth.com/mod/directionhud")) {
            if (message.getString().equals("")) return;
            if (client.player != null) {
                if (client.isInSingleplayer() && PlayerData.get.hud.state(
                        Player.of(Objects.requireNonNull(DirectionHUD.server.getPlayerManager().getPlayer(client.player.getUuid()))))) {
                    client.player.sendMessage(msg.b());
                } else if (DirectionHUDClient.onSupportedServer && DirectionHUDClient.hudState) {
                    client.player.sendMessage(msg.b());
                }
            }
        }
    }
}