package io.github.runjief.handshakecache.packet;

import java.util.function.IntSupplier;

public abstract class HandshakeCacheLoginPacket implements IntSupplier {

    private int loginIndex;

    public int getLoginIndex() {
        return loginIndex;
    }

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    @Override
    public int getAsInt() {
        return getLoginIndex();
    }
}
