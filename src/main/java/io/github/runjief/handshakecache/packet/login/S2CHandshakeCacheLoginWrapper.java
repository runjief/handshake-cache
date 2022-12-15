package io.github.runjief.handshakecache.packet.login;

import io.github.runjief.handshakecache.packet.ClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CHandshakeCacheLoginWrapper extends HandshakeCacheLoginPacket {

    private final ResourceLocation channel;
    private final ByteBuf data;

    public S2CHandshakeCacheLoginWrapper(ResourceLocation channel, ByteBuf data) {
        this.channel = channel;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(channel);
        buf.writeVarInt(data.readableBytes());
        buf.writeBytes(data);
    }

    public static S2CHandshakeCacheLoginWrapper decode(FriendlyByteBuf buf) {
        var channel = buf.readResourceLocation();
        var len = buf.readVarInt();
        return new S2CHandshakeCacheLoginWrapper(channel, buf.readSlice(len));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        return ClientHandler.handleLoginPacket(ctx.get(), channel, data, getLoginIndex());
    }
}
