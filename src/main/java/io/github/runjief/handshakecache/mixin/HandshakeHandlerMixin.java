package io.github.runjief.handshakecache.mixin;

import io.github.runjief.handshakecache.bridge.HandshakeHandlerBridge;
import io.github.runjief.handshakecache.packet.HandshakeCacheChannel;
import io.github.runjief.handshakecache.packet.ServerHandler;
import io.github.runjief.handshakecache.packet.login.S2CHandshakeCacheLoginWrapper;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.LoginWrapper;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = HandshakeHandler.class, remap = false)
public class HandshakeHandlerMixin implements HandshakeHandlerBridge {

    // @formatter:off
    @Shadow private List<NetworkRegistry.LoginPayload> messageList;
    @Shadow private List<Integer> sentMessages;
    @Shadow @Final private static LoginWrapper loginWrapper;
    @Shadow @Final private Connection manager;
    @Shadow private int packetPosition;
    // @formatter:on

    @Unique private List<NetworkRegistry.LoginPayload> pendingList;
    @Unique private boolean manifestSend = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void handshake_cache$hijackLogin(Connection networkManager, NetworkDirection side, CallbackInfo ci) {
        // a remote dedicated connection
        if (side == NetworkDirection.LOGIN_TO_CLIENT && !manager.isMemoryConnection()) {
            if (!this.messageList.isEmpty()) {
                this.pendingList = this.messageList;
                this.messageList = new ArrayList<>();
                this.sentMessages.add(HandshakeCacheChannel.getManifestId());
                this.manifestSend = true;
            }
        }
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void handshake_cache$sendManifest(CallbackInfoReturnable<Boolean> cir) {
        // skip sent(null) packets
        while (this.packetPosition < this.messageList.size()) {
            if (this.messageList.get(this.packetPosition) == null) {
                this.packetPosition++;
            } else {
                break;
            }
        }
        if (manifestSend) {
            ServerHandler.sendManifest(this.pendingList, loginWrapper, this.manager);
            manifestSend = false;
        }
    }

    @Override
    public void handshake_cacheClientMissing() {
        this.messageList = this.pendingList;
        this.pendingList = null;
        this.sentMessages.clear();
    }

    @Override
    public void handshake_cacheClientHit(int[] packets) {
        for (int packet : packets) {
            this.pendingList.set(packet, null);
            this.sentMessages.add(packet);
        }
        for (var payload : this.pendingList) {
            if (payload != null) {
                var wrapper = new S2CHandshakeCacheLoginWrapper(payload.getChannelName(), payload.getData());
                var buffer = new FriendlyByteBuf(Unpooled.buffer());
                HandshakeCacheChannel.channel().encodeMessage(wrapper, buffer);
                this.messageList.add(new NetworkRegistry.LoginPayload(
                    buffer,
                    HandshakeCacheChannel.getChannelName(),
                    payload.getMessageContext()
                ));
            } else {
                this.messageList.add(null);
            }
        }
    }
}
