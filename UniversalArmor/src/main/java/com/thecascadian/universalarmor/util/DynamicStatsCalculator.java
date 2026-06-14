package com.thecascadian.universalarmor.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DynamicStatsCalculator {

    public static int getDurabilityMultiplier(Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (name.contains("netherite") || name.contains("obsidian")) return 37;
        if (name.contains("diamond") || name.contains("steel") || name.contains("titanium")) return 33;
        if (name.contains("iron") || name.contains("bronze") || name.contains("copper") || name.contains("tin")) return 15;
        if (name.contains("gold") || name.contains("wood") || name.contains("leather")) return 7;
        if (name.contains("stone") || name.contains("cobble")) return 10;

        Block block = Block.byItem(item);
        if (block != Blocks.AIR) {
            float hardness = block.defaultBlockState().getDestroySpeed(null, null);
            if (hardness < 0) return 40;
            if (hardness == 0) return 5;
            return Math.clamp((int) (hardness * 5), 5, 40);
        }

        Rarity rarity = item.getDefaultInstance().getRarity();
        return switch (rarity) {
            case EPIC -> 40;
            case RARE -> 30;
            case UNCOMMON -> 20;
            default -> 12;
        };
    }

    public static int getDefense(Item item, ArmorItem.Type type) {
        int multiplier = getDurabilityMultiplier(item);
        float quality = multiplier / 30.0f;
        int baseDefense = switch (type) {
            case HELMET -> 3;
            case CHESTPLATE -> 8;
            case LEGGINGS -> 6;
            case BOOTS -> 3;
            default -> 1;
        };
        return Math.clamp(Math.round(baseDefense * quality), 1, 10);
    }

    public static float getToughness(Item item) {
        int multiplier = getDurabilityMultiplier(item);
        if (multiplier >= 33) return 3.0f;
        if (multiplier >= 20) return 1.5f;
        return 0.0f;
    }

    public static int getMaterialColor(Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (name.contains("iron") || name.contains("tin") || name.contains("silver") || name.contains("lead")) return 0xC0C0C0;
        if (name.contains("gold") || name.contains("electrum")) return 0xFED83D;
        if (name.contains("copper") || name.contains("bronze")) return 0xE07040;
        if (name.contains("diamond") || name.contains("prismarine") || name.contains("sapphire")) return 0x33EBFC;
        if (name.contains("netherite") || name.contains("obsidian") || name.contains("coal")) return 0x2A2A2A;
        if (name.contains("emerald")) return 0x17DD62;
        if (name.contains("redstone") || name.contains("ruby")) return 0xFF0000;
        if (name.contains("wood") || name.contains("planks") || name.contains("oak")) return 0x8F6E45;
        if (name.contains("stone") || name.contains("cobble") || name.contains("andesite")) return 0x7E7E7E;
        if (name.contains("dirt") || name.contains("mud") || name.contains("clay")) return 0x5C4033;
        if (name.contains("grass") || name.contains("leaves") || name.contains("slime")) return 0x5E973B;

        int hash = name.hashCode();
        int r = 50 + (Math.abs((hash & 0xFF0000) >> 16) % 180);
        int g = 50 + (Math.abs((hash & 0x00FF00) >> 8) % 180);
        int b = 50 + (Math.abs(hash & 0x0000FF) % 180);
        return (r << 16) | (g << 8) | b;
    }
}