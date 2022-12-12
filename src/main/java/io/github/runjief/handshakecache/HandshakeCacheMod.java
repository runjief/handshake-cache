package io.github.runjief.handshakecache;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
