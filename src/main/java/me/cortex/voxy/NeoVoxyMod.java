package me.cortex.voxy;

import me.cortex.voxy.client.VoxyClient;
import me.cortex.voxy.client.VoxyCommands;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("voxy")
public class NeoVoxyMod {

    public NeoVoxyMod(IEventBus modBus) {
        VoxyCommon.init();
        NeoForge.EVENT_BUS.addListener(NeoVoxyMod::onRegisterCommands);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(NeoVoxyMod::clientSetup);
        }
    }

    private static void clientSetup(FMLClientSetupEvent event) {
    }

    private static void onRegisterCommands(RegisterClientCommandsEvent event) {
        if (VoxyCommon.isAvailable()) {
            VoxyCommands.register(event.getDispatcher());
        }
    }

}
