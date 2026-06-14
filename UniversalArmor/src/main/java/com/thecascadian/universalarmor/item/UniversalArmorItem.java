package com.thecascadian.universalarmor.item;

import com.thecascadian.universalarmor.client.ArmorItemRenderer;
import com.thecascadian.universalarmor.component.ArmorMaterialComponent;
import com.thecascadian.universalarmor.registry.ModRegistries;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class UniversalArmorItem extends ArmorItem {

    private static final ArmorMaterial DYNAMIC_ARMOR_MATERIAL = new ArmorMaterial(
        Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.HELMET, 1);
            map.put(ArmorItem.Type.CHESTPLATE, 1);
            map.put(ArmorItem.Type.LEGGINGS, 1);
            map.put(ArmorItem.Type.BOOTS, 1);
        }), 10, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.EMPTY,
        List.of(new ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("iron"), "", false)),
        0.0f, 0.0f
    );

    public UniversalArmorItem(ArmorItem.Type type, Properties properties) {
        super(Holder.direct(DYNAMIC_ARMOR_MATERIAL), type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ArmorItemRenderer.getInstance();
            }
        });
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        ArmorMaterialComponent comp = stack.get(ModRegistries.ARMOR_MATERIAL_COMPONENT.get());
        if (comp != null) {
            if (entity.level().isClientSide()) {
                return com.thecascadian.universalarmor.client.ClientArmorTextureHelper.getTexture(comp.material().value(), innerModel);
            }
        }
        return super.getArmorTexture(stack, entity, slot, layer, innerModel);
    }

    @Override
    public Component getName(ItemStack stack) {
        ArmorMaterialComponent comp = stack.get(ModRegistries.ARMOR_MATERIAL_COMPONENT.get());
        if (comp != null) {
            Component materialName = comp.material().value().getDescription();
            return Component.translatable("item.universalarmor.dynamic_format", materialName,
                   Component.translatable(this.getDescriptionId() + ".generic"));
        }
        return super.getName(stack);
    }
}