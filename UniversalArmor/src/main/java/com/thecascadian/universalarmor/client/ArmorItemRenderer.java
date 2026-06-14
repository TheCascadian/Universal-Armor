package com.thecascadian.universalarmor.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.*;
import com.thecascadian.universalarmor.component.ArmorMaterialComponent;
import com.thecascadian.universalarmor.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.*;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ArmorItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static ArmorItemRenderer instance;

    public static ArmorItemRenderer getInstance() {
        if (instance == null) {
            Minecraft mc = Minecraft.getInstance();
            instance = new ArmorItemRenderer(mc);
        }
        return instance;
    }

    private ArmorItemRenderer(Minecraft mc) {
        super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
    }

    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        ArmorMaterialComponent comp = stack.get(ModRegistries.ARMOR_MATERIAL_COMPONENT.get());
        if (comp == null || !(stack.getItem() instanceof ArmorItem armorItem)) return;

        ResourceLocation overlay = overlayFor(armorItem.getType());
        ResourceLocation composite = getOrCreate(comp.material().value(), overlay);

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentCull(composite));
        Matrix4f mat = poseStack.last().pose();

        vc.addVertex(mat, 0F, 1F, 0.53125F).setColor(255, 255, 255, 255).setUv(0F, 0F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 0F, 0F, 0.53125F).setColor(255, 255, 255, 255).setUv(0F, 1F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 1F, 0F, 0.53125F).setColor(255, 255, 255, 255).setUv(1F, 1F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        vc.addVertex(mat, 1F, 1F, 0.53125F).setColor(255, 255, 255, 255).setUv(1F, 0F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);

        vc.addVertex(mat, 1F, 1F, 0.46875F).setColor(255, 255, 255, 255).setUv(1F, 0F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        vc.addVertex(mat, 1F, 0F, 0.46875F).setColor(255, 255, 255, 255).setUv(1F, 1F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        vc.addVertex(mat, 0F, 0F, 0.46875F).setColor(255, 255, 255, 255).setUv(0F, 1F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
        vc.addVertex(mat, 0F, 1F, 0.46875F).setColor(255, 255, 255, 255).setUv(0F, 0F).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, -1);
    }

    private ResourceLocation overlayFor(ArmorItem.Type type) {
        return switch (type) {
            case HELMET     -> ResourceLocation.fromNamespaceAndPath("universalarmor", "textures/item/helmet_overlay.png");
            case CHESTPLATE -> ResourceLocation.fromNamespaceAndPath("universalarmor", "textures/item/chestplate_overlay.png");
            case LEGGINGS   -> ResourceLocation.fromNamespaceAndPath("universalarmor", "textures/item/leggings_overlay.png");
            case BOOTS      -> ResourceLocation.fromNamespaceAndPath("universalarmor", "textures/item/boots_overlay.png");
            default         -> throw new IllegalStateException("Unexpected type: " + type);
        };
    }

    private ResourceLocation getOrCreate(Item material, ResourceLocation overlay) {
        String key = BuiltInRegistries.ITEM.getKey(material) + "|" + overlay;
        return CACHE.computeIfAbsent(key, k -> generate(material, overlay));
    }

    private ResourceLocation generate(Item material, ResourceLocation overlay) {
        Minecraft mc = Minecraft.getInstance();
        try {
            var model = mc.getItemRenderer().getItemModelShaper().getItemModel(material.getDefaultInstance());
            var sprite = model.getParticleIcon(net.neoforged.neoforge.client.model.data.ModelData.EMPTY);
            ResourceLocation spriteLoc = sprite.contents().name();

            ResourceLocation srcPath = ResourceLocation.fromNamespaceAndPath(
                spriteLoc.getNamespace(),
                "textures/" + spriteLoc.getPath() + ".png"
            );

            Optional<Resource> srcRes = mc.getResourceManager().getResource(srcPath);
            Optional<Resource> ovlRes = mc.getResourceManager().getResource(overlay);

            if (srcRes.isEmpty()) return overlay;
            if (ovlRes.isEmpty()) return srcPath;

            NativeImage src = NativeImage.read(srcRes.get().open());
            NativeImage ovl = NativeImage.read(ovlRes.get().open());

            int w = ovl.getWidth(), h = ovl.getHeight();
            NativeImage out = new NativeImage(w, h, true);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    int srcPx    = src.getPixelRGBA(x % src.getWidth(), y % src.getHeight());
                    int ovlAlpha = (ovl.getPixelRGBA(x, y) >> 24) & 0xFF;
                    out.setPixelRGBA(x, y, (ovlAlpha << 24) | (srcPx & 0x00FFFFFF));
                }
            }

            src.close();
            ovl.close();

            ResourceLocation matKey = BuiltInRegistries.ITEM.getKey(material);
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("universalarmor",
                "dyn_" + matKey.getNamespace() + "_" + matKey.getPath() + "_" + overlay.getPath().hashCode());
            mc.getTextureManager().register(loc, new DynamicTexture(out));
            return loc;

        } catch (Exception e) {
            e.printStackTrace();
            return overlay;
        }
    }
}