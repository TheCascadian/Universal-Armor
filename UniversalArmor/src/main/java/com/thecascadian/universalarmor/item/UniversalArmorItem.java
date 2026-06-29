package com.thecascadian.universalarmor.item;

import com.thecascadian.universalarmor.component.ArmorMaterialComponent;
import com.thecascadian.universalarmor.registry.ModRegistries;
import java.util.EnumMap;
import java.util.List;
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

public class UniversalArmorItem extends ArmorItem {

  private static final ArmorMaterial DYNAMIC_ARMOR_MATERIAL = new ArmorMaterial(
    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
      map.put(ArmorItem.Type.HELMET, 1);
      map.put(ArmorItem.Type.CHESTPLATE, 1);
      map.put(ArmorItem.Type.LEGGINGS, 1);
      map.put(ArmorItem.Type.BOOTS, 1);
    }),
    10,
    SoundEvents.ARMOR_EQUIP_GENERIC,
    () -> Ingredient.EMPTY,
    List.of(
      new ArmorMaterial.Layer(
        ResourceLocation.withDefaultNamespace("iron"),
        "",
        false
      )
    ),
    0.0f,
    0.0f
  );

  public UniversalArmorItem(ArmorItem.Type type, Properties properties) {
    super(Holder.direct(DYNAMIC_ARMOR_MATERIAL), type, properties);
  }

  @Override
  public ResourceLocation getArmorTexture(
    ItemStack stack,
    Entity entity,
    EquipmentSlot slot,
    ArmorMaterial.Layer layer,
    boolean innerModel
  ) {
    ArmorMaterialComponent comp = stack.get(
      ModRegistries.ARMOR_MATERIAL_COMPONENT.get()
    );
    if (comp != null) {
      // Because ClientArmorTextureHelper's method signature doesn't contain client-only classes,
      // this is perfectly safe to call here without crashing dedicated servers.
      return com.thecascadian.universalarmor.client.ClientArmorTextureHelper.getTexture(
        comp.material().value(),
        innerModel
      );
    }
    return super.getArmorTexture(stack, entity, slot, layer, innerModel);
  }

  @Override
  public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
    ArmorMaterialComponent comp = toRepair.get(
      ModRegistries.ARMOR_MATERIAL_COMPONENT.get()
    );
    // Allow repair if the item used in the anvil matches the dynamically stored material
    if (comp != null && comp.material().value() == repair.getItem()) {
      return true;
    }
    return super.isValidRepairItem(toRepair, repair);
  }

  @Override
  public Component getName(ItemStack stack) {
    ArmorMaterialComponent comp = stack.get(
      ModRegistries.ARMOR_MATERIAL_COMPONENT.get()
    );
    if (comp != null) {
      Component materialName = comp.material().value().getDescription();
      return Component.translatable(
        "item.universalarmor.dynamic_format",
        materialName,
        Component.translatable(this.getDescriptionId() + ".generic")
      );
    }
    return super.getName(stack);
  }
}
