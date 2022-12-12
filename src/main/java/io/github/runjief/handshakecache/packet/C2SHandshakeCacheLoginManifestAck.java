package io.github.runjief.handshakecache.packet;

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
        return new C2SHandshakeCacheLoginManifestAck(buf.readVarIntArray());
    }

    public static void handle(HandshakeHandler handler, C2SHandshakeCacheLoginManifestAck msg, Supplier<NetworkEvent.Context> ctx) {

    }
}
