package com.thecascadian.universalarmor.recipe;

import com.thecascadian.universalarmor.component.ArmorMaterialComponent;
import com.thecascadian.universalarmor.registry.ModRegistries;
import com.thecascadian.universalarmor.util.DynamicStatsCalculator;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class DynamicArmorRecipe extends CustomRecipe {

    public DynamicArmorRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        Item firstItem = null;
        int count = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (firstItem == null) {
                    firstItem = stack.getItem();
                    if (firstItem instanceof ArmorItem || firstItem.getMaxStackSize(stack) == 1) {
                        return false;
                    }
                } else if (stack.getItem() != firstItem) {
                    return false;
                }
                count++;
            }
        }

        return (count == 4 || count == 5 || count == 7 || count == 8) && detectArmorShape(input, count);
    }

    private boolean detectArmorShape(CraftingInput input, int count) {
        int width = input.width();
        int height = input.height();

        if (width != 3 || (height != 2 && height != 3)) {
            return false;
        }

        boolean[][] grid = new boolean[height][width];
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = !input.getItem(r * width + c).isEmpty();
            }
        }

        if (count == 5 && height == 2) { // Helmet
            return grid[0][0] && grid[0][1] && grid[0][2]
                && grid[1][0] && !grid[1][1] && grid[1][2];
        }
        if (count == 8 && height == 3) { // Chestplate
            return grid[0][0] && !grid[0][1] && grid[0][2]
                && grid[1][0] && grid[1][1] && grid[1][2]
                && grid[2][0] && grid[2][1] && grid[2][2];
        }
        if (count == 7 && height == 3) { // Leggings
            return grid[0][0] && grid[0][1] && grid[0][2]
                && grid[1][0] && !grid[1][1] && grid[1][2]
                && grid[2][0] && !grid[2][1] && grid[2][2];
        }
        if (count == 4 && height == 2) { // Boots
            return grid[0][0] && !grid[0][1] && grid[0][2]
                && grid[1][0] && !grid[1][1] && grid[1][2];
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack firstStack = ItemStack.EMPTY;
        int ingredientCount = 0;
        
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (!s.isEmpty()) {
                if (firstStack.isEmpty()) firstStack = s;
                ingredientCount++;
            }
        }
        
        if (firstStack.isEmpty()) return ItemStack.EMPTY;

        Item materialItem = firstStack.getItem();

        ItemStack result = switch (ingredientCount) {
            case 5 -> new ItemStack(ModRegistries.UNIVERSAL_HELMET.get());
            case 8 -> new ItemStack(ModRegistries.UNIVERSAL_CHESTPLATE.get());
            case 7 -> new ItemStack(ModRegistries.UNIVERSAL_LEGGINGS.get());
            case 4 -> new ItemStack(ModRegistries.UNIVERSAL_BOOTS.get());
            default -> ItemStack.EMPTY;
        };

        if (result.isEmpty()) return ItemStack.EMPTY;

        ArmorItem armorItem = (ArmorItem) result.getItem();
        ArmorItem.Type slotType = armorItem.getType();

        Holder<Item> itemHolder = BuiltInRegistries.ITEM.wrapAsHolder(materialItem);
        result.set(ModRegistries.ARMOR_MATERIAL_COMPONENT.get(), new ArmorMaterialComponent(itemHolder));

        int baseDurability = switch (slotType) {
            case HELMET -> 11;
            case CHESTPLATE -> 16;
            case LEGGINGS -> 15;
            case BOOTS -> 13;
            default -> 12;
        };
        result.set(DataComponents.MAX_DAMAGE, baseDurability * DynamicStatsCalculator.getDurabilityMultiplier(materialItem));

        EquipmentSlotGroup targetSlot = switch (slotType) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };

        ItemAttributeModifiers.Builder attributesBuilder = ItemAttributeModifiers.builder();
        attributesBuilder.add(Attributes.ARMOR, new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("universalarmor", "defense"),
                DynamicStatsCalculator.getDefense(materialItem, slotType),
                AttributeModifier.Operation.ADD_VALUE
                ), targetSlot);

        float toughness = DynamicStatsCalculator.getToughness(materialItem);
        if (toughness > 0) {
            attributesBuilder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("universalarmor", "toughness"),
                    toughness,
                    AttributeModifier.Operation.ADD_VALUE
            ), targetSlot);
        }
        result.set(DataComponents.ATTRIBUTE_MODIFIERS, attributesBuilder.build());

        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ModRegistries.UNIVERSAL_CHESTPLATE.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRegistries.DYNAMIC_ARMOR_SERIALIZER.get();
    }
}