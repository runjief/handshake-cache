package io.github.runjief.handshakecache.cache;

import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.concurrent.Callable;

public interface LocalRepository {

    Optional<Callable<ByteBuf>> find(String tag);

    void put(String tag, ByteBuf data);

    void tick();
}
