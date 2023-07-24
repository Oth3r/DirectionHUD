package one.oth3r.directionhud.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnPlayerWorldChanged {
    @Inject(at = @At("HEAD"), method = "worldChanged(Lnet/minecraft/server/world/ServerWorld;)V")
    public void worldChangedCallback(ServerWorld world, CallbackInfo info) {
        Player player = Player.of((ServerPlayerEntity) (Object) this);
        Events.playerChangeWorld(player,Utl.dim.format(world.getRegistryKey().getValue()),player.getDimension());
    }
}
