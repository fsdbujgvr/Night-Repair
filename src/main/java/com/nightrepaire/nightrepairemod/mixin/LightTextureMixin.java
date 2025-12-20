package com.nightrepaire.nightrepairemod.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    // Redirect the call to GameRenderer#getNightVisionScale inside LightTexture.updateLightTexture
    // to blend the returned scale with the current gamma setting.
    @Redirect(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F")
    )
    private float nightrepair$blendNightVisionWithGamma(net.minecraft.world.entity.LivingEntity entity,
                                                        float partialTicks) {
        float base = net.minecraft.client.renderer.GameRenderer.getNightVisionScale(entity, partialTicks);
        double gammaRaw = Minecraft.getInstance().options.gamma().get();
        double gamma = Math.max(0.0, Math.min(gammaRaw, 1.0));
        // 新公式(先慢后快): 使用平方曲线
        // gamma=0.0 -> factor=1.0
        // gamma=0.5 -> factor=1.5 (原线性为2.0)
        // gamma=1.0 -> factor=3.0
        float factor = (float)(1.0 + Math.pow(gamma, 1.8) * 2.0);
        float adjusted = base * factor;
        return Math.max(0.0f, adjusted); // 移除上限限制，允许超过1.0
    }

    private static int applyGammaBlend(int argb, float nightVisionScale, double gammaRaw) {
        int a = (argb >>> 24) & 0xFF;
        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = (argb) & 0xFF;

        double gamma = Math.max(0.0, Math.min(gammaRaw, 1.0));
        float p = (float) (gamma * Math.min(nightVisionScale, 1.0f) * 0.6f);

        r = blendToWhite(r, p);
        g = blendToWhite(g, p);
        b = blendToWhite(b, p);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int blendToWhite(int channel, float p) {
        float c = channel / 255.0f;
        c = c + (1.0f - c) * p;
        int out = Math.round(c * 255.0f);
        if (out < 0) out = 0;
        if (out > 255) out = 255;
        return out;
    }
}