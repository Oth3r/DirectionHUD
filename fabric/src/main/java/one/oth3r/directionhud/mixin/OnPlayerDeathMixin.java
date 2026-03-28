package one.oth3r.directionhud.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.DPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class OnPlayerDeathMixin {
    @Inject(at = @At("HEAD"), method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V")
    public void onDeathCallback(DamageSource source, CallbackInfo onDeathCallbackInfoReturnable) {
        DPlayer player = new DPlayer((ServerPlayer) (Object) this);
        Events.playerDeath(player,new Loc(player));
    }
}
