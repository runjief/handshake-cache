package io.github.runjief.handshakecache;

import io.github.runjief.handshakecache.packet.HandshakeCacheChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod(HandshakeCacheMod.MODID)
public class HandshakeCacheMod {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "handshake_cache";

    public HandshakeCacheMod() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
            () -> new IExtensionPoint.DisplayTest(
                () -> NetworkConstants.IGNORESERVERONLY,
                (a, b) -> true
            ));
        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> {
            Objects.requireNonNull(HandshakeCacheChannel.channel(), "channel");
        });
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> HandshakeCacheConfig::register);
    }
}
