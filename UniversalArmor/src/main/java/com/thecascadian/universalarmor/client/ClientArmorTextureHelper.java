package com.thecascadian.universalarmor.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ClientArmorTextureHelper {
    public static ResourceLocation getTexture(Item material, boolean inner) {
        return DynamicArmorTextureManager.getOrCreateTexture(material, inner);
    }
}