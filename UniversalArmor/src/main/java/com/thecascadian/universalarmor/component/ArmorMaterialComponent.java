package com.thecascadian.universalarmor.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record ArmorMaterialComponent(Holder<Item> material) {
    public static final Codec<ArmorMaterialComponent> CODEC = BuiltInRegistries.ITEM.holderByNameCodec()
            .xmap(ArmorMaterialComponent::new, ArmorMaterialComponent::material);

    // Add StreamCodec for network synchronization
    public static final StreamCodec<RegistryFriendlyByteBuf, ArmorMaterialComponent> STREAM_CODEC = 
            ByteBufCodecs.holderRegistry(Registries.ITEM).map(ArmorMaterialComponent::new, ArmorMaterialComponent::material);
}