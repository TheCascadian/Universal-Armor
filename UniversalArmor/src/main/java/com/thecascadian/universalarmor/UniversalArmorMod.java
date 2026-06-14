package com.thecascadian.universalarmor;

import com.thecascadian.universalarmor.registry.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(UniversalArmorMod.MOD_ID)
public class UniversalArmorMod {
    public static final String MOD_ID = "universalarmor";

    public UniversalArmorMod(IEventBus modEventBus) {
        ModRegistries.ITEMS.register(modEventBus);
        ModRegistries.DATA_COMPONENT_TYPES.register(modEventBus);
        ModRegistries.RECIPE_SERIALIZERS.register(modEventBus);
    }
}