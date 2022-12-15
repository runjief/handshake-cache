package io.github.runjief.handshakecache.packet.login;

import io.github.runjief.handshakecache.HandshakeCacheMod;
import io.github.runjief.handshakecache.bridge.HandshakeHandlerBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SHandshakeCacheLoginManifestAck extends HandshakeCacheLoginPacket {

    private final int[] handled;

    public C2SHandshakeCacheLoginManifestAck(int[] handled) {
        this.handled = handled;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarIntArray(this.handled);
    }

    public static C2SHandshakeCacheLoginManifestAck decode(FriendlyByteBuf buf) {
        return new C2SHandshakeCacheLoginManifestAck(buf.readVarIntArray(Math.min(1 << 16, buf.readableBytes())));
    }

    public static void handle(HandshakeHandler handler, C2SHandshakeCacheLoginManifestAck msg, Supplier<NetworkEvent.Context> ctx) {
        HandshakeCacheMod.LOGGER.debug("Client cache hits: {} packets", msg.handled.length);
        ((HandshakeHandlerBridge) handler).handshake_cacheClientHit(msg.handled);
        ctx.get().setPacketHandled(true);
    }
}
