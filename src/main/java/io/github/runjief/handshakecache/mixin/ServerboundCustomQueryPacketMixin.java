package io.github.runjief.handshakecache.mixin;

import io.github.runjief.handshakecache.packet.HandshakeCacheChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ICustomPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(ServerboundCustomQueryPacket.class)
public class ServerboundCustomQueryPacketMixin implements ICustomPacket<ServerboundCustomQueryPacket> {

    @Shadow @Final @Nullable private FriendlyByteBuf data;

    @Override
    public ResourceLocation getName() {
        if (getIndex() == HandshakeCacheChannel.getManifestId() && this.data == null) {
            return HandshakeCacheChannel.getChannelName();
        }
        return ICustomPacket.super.getName();
    }
}
