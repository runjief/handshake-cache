package io.github.runjief.handshakecache.packet;

import com.google.common.hash.Hashing;
import io.github.runjief.handshakecache.HandshakeCacheHandles;
import io.github.runjief.handshakecache.HandshakeCacheMod;
import io.github.runjief.handshakecache.bridge.HandshakeHandlerBridge;
import io.github.runjief.handshakecache.packet.login.S2CHandshakeCacheLoginManifest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.LoginWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;

import java.util.List;

public class ServerHandler {

    public static <T extends NetworkEvent> void onClientMissing(T event) {
        var context = event.getSource().get();
        if (context.getDirection() == NetworkDirection.LOGIN_TO_SERVER
            && event.getLoginIndex() == HandshakeCacheChannel.getManifestId() && event.getPayload() == null) {
            HandshakeCacheMod.LOGGER.debug("Client handshake_cache missing, revert to forge handshake");
            ((HandshakeHandlerBridge) HandshakeCacheHandles.getHandshakeHandler(context.getNetworkManager())).handshake_cacheClientMissing();
            context.setPacketHandled(true);
        }
    }

    public static void sendManifest(List<NetworkRegistry.LoginPayload> payloads, LoginWrapper loginWrapper, Connection connection) {
        var tags = payloads.stream().map(payload -> {
            var head = new FriendlyByteBuf(Unpooled.buffer());
            head.writeResourceLocation(payload.getChannelName());
            head.writeVarInt(payload.getData().readableBytes());
            var hashCode = Hashing.sha256().hashBytes(Unpooled.wrappedBuffer(head, payload.getData()).nioBuffer());
            return hashCode.asBytes();
        }).toArray(byte[][]::new);
        var target = new FriendlyByteBuf(Unpooled.buffer());
        var message = new S2CHandshakeCacheLoginManifest(tags);
        message.setLoginIndex(HandshakeCacheChannel.getManifestId());
        HandshakeCacheChannel.channel().encodeMessage(message, target);
        HandshakeCacheHandles.sendServerToClientLoginPacket(loginWrapper, HandshakeCacheChannel.getChannelName(), target, HandshakeCacheChannel.getManifestId(), connection);
    }
}
