package io.github.runjief.handshakecache.packet;

import io.github.runjief.handshakecache.HandshakeCacheHandles;
import io.github.runjief.handshakecache.HandshakeCacheMod;
import io.github.runjief.handshakecache.packet.login.C2SHandshakeCacheLoginManifestAck;
import io.github.runjief.handshakecache.packet.login.S2CHandshakeCacheLoginManifest;
import io.github.runjief.handshakecache.packet.login.S2CHandshakeCacheLoginWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.ThreadLocalRandom;

public class HandshakeCacheChannel {

    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL;
    private static final int MANIFEST_ID;
    private static final ResourceLocation CHANNEL_NAME;

    static {
        CHANNEL_NAME = new ResourceLocation(HandshakeCacheMod.MODID, "ch");
        CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_NAME,
            () -> VERSION,
            NetworkRegistry.acceptMissingOr(VERSION),
            NetworkRegistry.acceptMissingOr(VERSION)
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
            .add();
        var ni = HandshakeCacheHandles.findTarget(CHANNEL_NAME).orElseThrow();
        ni.addListener(ServerHandler::onClientMissing);
        MANIFEST_ID = ThreadLocalRandom.current().nextInt(1 << 15, 1 << 16); // this is big enough
    }

    public static SimpleChannel channel() {
        return CHANNEL;
    }

    public static int getManifestId() {
        return MANIFEST_ID;
    }

    public static ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
