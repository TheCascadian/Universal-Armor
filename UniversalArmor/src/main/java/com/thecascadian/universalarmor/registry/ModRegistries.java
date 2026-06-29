package com.thecascadian.universalarmor.registry;

import com.thecascadian.universalarmor.UniversalArmorMod;
import com.thecascadian.universalarmor.component.ArmorMaterialComponent;
import com.thecascadian.universalarmor.item.UniversalArmorItem;
import com.thecascadian.universalarmor.recipe.DynamicArmorRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRegistries {

  public static final DeferredRegister.Items ITEMS =
    DeferredRegister.createItems(UniversalArmorMod.MOD_ID);
  public static final DeferredRegister<
    DataComponentType<?>
  > DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(
    Registries.DATA_COMPONENT_TYPE,
    UniversalArmorMod.MOD_ID
  );
  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
    DeferredRegister.create(
      Registries.RECIPE_SERIALIZER,
      UniversalArmorMod.MOD_ID
    );

  public static final DeferredHolder<
    DataComponentType<?>,
    DataComponentType<ArmorMaterialComponent>
  > ARMOR_MATERIAL_COMPONENT = DATA_COMPONENT_TYPES.register("material", () ->
    DataComponentType.<ArmorMaterialComponent>builder()
      .persistent(ArmorMaterialComponent.CODEC)
      .networkSynchronized(ArmorMaterialComponent.STREAM_CODEC) // Syncs to the client!
      .build()
  );

  public static final DeferredHolder<
    Item,
    UniversalArmorItem
  > UNIVERSAL_HELMET = ITEMS.register("universal_helmet", () ->
    new UniversalArmorItem(
      ArmorItem.Type.HELMET,
      new Item.Properties().durability(11) // Adds standard damage components
    )
  );
  public static final DeferredHolder<
    Item,
    UniversalArmorItem
  > UNIVERSAL_CHESTPLATE = ITEMS.register("universal_chestplate", () ->
    new UniversalArmorItem(
      ArmorItem.Type.CHESTPLATE,
      new Item.Properties().durability(16) // Adds standard damage components
    )
  );
  public static final DeferredHolder<
    Item,
    UniversalArmorItem
  > UNIVERSAL_LEGGINGS = ITEMS.register("universal_leggings", () ->
    new UniversalArmorItem(
      ArmorItem.Type.LEGGINGS,
      new Item.Properties().durability(15) // Adds standard damage components
    )
  );
  public static final DeferredHolder<Item, UniversalArmorItem> UNIVERSAL_BOOTS =
    ITEMS.register("universal_boots", () ->
      new UniversalArmorItem(
        ArmorItem.Type.BOOTS,
        new Item.Properties().durability(13) // Adds standard damage components
      )
    );

  public static final DeferredHolder<
    RecipeSerializer<?>,
    SimpleCraftingRecipeSerializer<DynamicArmorRecipe>
  > DYNAMIC_ARMOR_SERIALIZER = RECIPE_SERIALIZERS.register(
    "dynamic_armor",
    () -> new SimpleCraftingRecipeSerializer<>(DynamicArmorRecipe::new)
  );
}
