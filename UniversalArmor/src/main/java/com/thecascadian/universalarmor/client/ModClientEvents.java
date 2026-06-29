package com.thecascadian.universalarmor.client;

import com.thecascadian.universalarmor.UniversalArmorMod;
import com.thecascadian.universalarmor.registry.ModRegistries;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = UniversalArmorMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        // This is the correct, modern way to register a BlockEntityWithoutLevelRenderer (BEWLR)
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ArmorItemRenderer.getInstance();
            }
        }, 
        ModRegistries.UNIVERSAL_HELMET.get(),
        ModRegistries.UNIVERSAL_CHESTPLATE.get(),
        ModRegistries.UNIVERSAL_LEGGINGS.get(),
        ModRegistries.UNIVERSAL_BOOTS.get());
    }
}