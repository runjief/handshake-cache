package io.github.runjief.handshakecache.packet;

import io.github.runjief.handshakecache.HandshakeCacheMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class HandshakeCacheChannel {

    private static final SimpleChannel CHANNEL;

    static {
        CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(HandshakeCacheMod.MODID, "ch"),
            () -> "1",
            s -> true,
            s -> true
        );
        CHANNEL.messageBuilder(S2CHandshakeCacheLoginManifest.class, 0, NetworkDirection.LOGIN_TO_CLIENT)
            .loginIndex(S2CHandshakeCacheLoginManifest::getLoginIndex, S2CHandshakeCacheLoginManifest::setLoginIndex)
            .encoder(S2CHandshakeCacheLoginManifest::encode)
            .decoder(S2CHandshakeCacheLoginManifest::decode)
            .consumer(S2CHandshakeCacheLoginManifest::handle)
            .add();
        CHANNEL.messageBuilder(C2SHandshakeCacheLoginManifestAck.class, 1, NetworkDirection.LOGIN_TO_SERVER)
            .loginIndex(C2SHandshakeCacheLoginManifestAck::getLoginIndex, C2SHandshakeCacheLoginManifestAck::setLoginIndex)
            .encoder(C2SHandshakeCacheLoginManifestAck::encode)
            .decoder(C2SHandshakeCacheLoginManifestAck::decode)
            .consumer(HandshakeHandler.indexFirst(C2SHandshakeCacheLoginManifestAck::handle))
            .add();
        CHANNEL.messageBuilder(S2CHandshakeCacheLoginWrapper.class, 2, NetworkDirection.LOGIN_TO_CLIENT)
            .loginIndex(S2CHandshakeCacheLoginWrapper::getLoginIndex, S2CHandshakeCacheLoginWrapper::setLoginIndex)
            .encoder(S2CHandshakeCacheLoginWrapper::encode)
            .decoder(S2CHandshakeCacheLoginWrapper::decode)
            .consumer(S2CHandshakeCacheLoginWrapper::handle)
    }

    public static SimpleChannel channel() {
        return CHANNEL;
    }
}
