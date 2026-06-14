package com.thecascadian.universalarmor.client;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class DynamicArmorTextureManager {

  private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

  public static ResourceLocation getOrCreateTexture(
    Item material,
    boolean innerModel
  ) {
    String cacheKey =
      BuiltInRegistries.ITEM.getKey(material).toString() +
      (innerModel ? "_inner" : "_outer");
    return CACHE.computeIfAbsent(cacheKey, key ->
      generateTiledTexture(material, innerModel)
    );
  }

  private static ResourceLocation generateTiledTexture(
    Item material,
    boolean innerModel
  ) {
    Minecraft mc = Minecraft.getInstance();
    ResourceLocation key = BuiltInRegistries.ITEM.getKey(material);

    // Use the item's particle sprite to reliably find its base texture path
    var model = mc
      .getItemRenderer()
      .getItemModelShaper()
      .getItemModel(material.getDefaultInstance());
    var sprite = model.getParticleIcon(
      net.neoforged.neoforge.client.model.data.ModelData.EMPTY
    );
    ResourceLocation spriteLoc = sprite.contents().name();

    ResourceLocation sourcePath = ResourceLocation.fromNamespaceAndPath(
      spriteLoc.getNamespace(),
      "textures/" + spriteLoc.getPath() + ".png"
    );

    ResourceLocation maskPath = innerModel
      ? ResourceLocation.fromNamespaceAndPath(
          "minecraft",
          "textures/models/armor/iron_layer_2.png"
        )
      : ResourceLocation.fromNamespaceAndPath(
          "minecraft",
          "textures/models/armor/iron_layer_1.png"
        );

    try {
      Optional<Resource> sourceOpt = mc
        .getResourceManager()
        .getResource(sourcePath);
      Optional<Resource> maskOpt = mc
        .getResourceManager()
        .getResource(maskPath);
      // ... (rest of your generation code remains unchanged)
      if (sourceOpt.isPresent() && maskOpt.isPresent()) {
        try (
          InputStream sourceStream = sourceOpt.get().open();
          InputStream maskStream = maskOpt.get().open()
        ) {
          NativeImage originalBlock = NativeImage.read(sourceStream);
          NativeImage maskImage = NativeImage.read(maskStream);

          int origW = originalBlock.getWidth();
          int origH = originalBlock.getHeight();
          int maskW = maskImage.getWidth();
          int maskH = maskImage.getHeight();

          NativeImage resultImage = new NativeImage(maskW, maskH, true);

          for (int x = 0; x < maskW; x++) {
            for (int y = 0; y < maskH; y++) {
              int srcX = x % origW;
              int srcY = y % origH;
              int tiledColor = originalBlock.getPixelRGBA(srcX, srcY);

              int maskColor = maskImage.getPixelRGBA(x, y);
              int maskAlpha = (maskColor >> 24) & 0xFF;

              int finalColor = (maskAlpha << 24) | (tiledColor & 0x00FFFFFF);
              resultImage.setPixelRGBA(x, y, finalColor);
            }
          }

          originalBlock.close();
          maskImage.close();

          DynamicTexture dynamicTexture = new DynamicTexture(resultImage);
          String cleanName =
            key.getNamespace() +
            "_" +
            key.getPath() +
            (innerModel ? "_inner" : "_outer");
          return mc
            .getTextureManager()
            .register("universalarmor_" + cleanName, dynamicTexture);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return maskPath;
  }
}
