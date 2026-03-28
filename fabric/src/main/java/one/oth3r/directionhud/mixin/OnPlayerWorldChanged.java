package one.oth3r.directionhud.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.utils.DPlayer;
import one.oth3r.directionhud.utils.Utl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class OnPlayerWorldChanged {
    @Inject(at = @At("HEAD"), method = "triggerDimensionChangeTriggers(Lnet/minecraft/server/level/ServerLevel;)V")
    public void worldChangedCallback(ServerLevel world, CallbackInfo info) {
        DPlayer player = new DPlayer((ServerPlayer) (Object) this);
        Events.playerChangeWorld(player,Utl.dim.format(world.dimension()),player.getDimension());
    }
}
