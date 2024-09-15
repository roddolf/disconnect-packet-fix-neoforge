/**
 * Taken from: https://github.com/ascpixi/disconnect-packet-fix
 * And addapted for NeoForge
 */

package dev.roaguirr.disconnectPacketFix.mixin;

import dev.roaguirr.disconnectPacketFix.DisconnectPacketFixMod;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.codec.IdDispatchCodec;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Function;

@Mixin(IdDispatchCodec.class)
public abstract class ConnectionMixin {
    @Final
    @Shadow
    private Function<Object, ?> packetIdGetter;

    @Final
    @Shadow
    private Object2IntMap<Object> typeToIndex;

    @Inject(at = @At("HEAD"), method = "encode(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V", cancellable = true)
    private void encodeMixin(ByteBuf byteBuf, Object object, CallbackInfo info) {
        var packetId = this.packetIdGetter.apply(object);
        if (!this.typeToIndex.containsKey(packetId)) {
            if (Objects.equals(String.valueOf(packetId), "clientbound/minecraft:disconnect")) {
                DisconnectPacketFixMod.LOGGER.debug("Caught an invalid disconnect packet.");
                info.cancel();
            }
        }
    }
}