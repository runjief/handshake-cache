package io.github.runjief.handshakecache.bridge;

public interface HandshakeHandlerBridge {

    void handshake_cacheClientMissing();

    void handshake_cacheClientHit(int[] packets);
}
