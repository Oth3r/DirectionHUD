package one.oth3r.directionhud.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerDeathMixin {
    @Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
    public void onDeathCallback(DamageSource source, CallbackInfo onDeathCallbackInfoReturnable) {
        Player player = Player.of((ServerPlayerEntity) (Object) this);
        Events.playerDeath(player,new Loc(player));
    }
}
