package net.onpointcoding.extraarmourinfo.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.onpointcoding.extraarmourinfo.ExtraArmourInfo;
import net.onpointcoding.extraarmourinfo.config.SubCategoryConfig;
import net.onpointcoding.extraarmourinfo.config.statusbars.HotIconConfig;
import net.onpointcoding.extraarmourinfo.config.statusbars.KnockbackConfig;
import net.onpointcoding.extraarmourinfo.config.statusbars.ToughnessConfig;
import net.onpointcoding.extraarmourinfo.enums.PositionDisplayOption;
import net.onpointcoding.extraarmourinfo.enums.SideDisplayOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends DrawableHelper {
    public final Identifier EXTRA_ARMOUR_INFO_ICONS_TEXTURE = new Identifier("extraarmourinfo:textures/gui/extra_armour_info.png");

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledHeight;
    @Shadow
    private int scaledWidth;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    protected abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

    @Shadow
    @Final
    private PlayerListHud playerListHud;

    @Shadow
    protected abstract void renderHotbar(float tickDelta, MatrixStack matrices);

    @Shadow
    public abstract void tick(boolean paused);

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    public void renderExtraArmourInfoStatusBar(MatrixStack matrices, CallbackInfo ci) {
        ExtraArmourInfo extraArmourInfo = ExtraArmourInfo.getInstance();
        KnockbackConfig knockbackConfig = extraArmourInfo.getKnockbackResistanceConfig();
        ToughnessConfig toughnessConfig = extraArmourInfo.getToughnessConfig();

        if (!knockbackConfig.enabled && !toughnessConfig.enabled) return;

        PlayerEntity playerEntity = getCameraPlayer();

        if (playerEntity != null) {
            // just a copy of some of the math in the vanilla renderStatusBars method
            int m = scaledWidth / 2;
            int o = scaledHeight - 39;
            float f = (float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r - 10;

            int leftX = m - 91; // 91 is the icon furthest left
            int rightX = m + 91 - 9 * 9; // 91 is the icon furthest right
            int leftY = s - 10; // Leave 10 pixels for health bar
            int rightY = o - 10; // Leave 10 pixels for hunger bar

            int farLeftX = 1;
            int farMiddleRightX = scaledWidth - 10;
            int farRightX = scaledWidth - 1 - 9 * 9;
            int farMiddleY = (scaledHeight - 9 * 9) / 2;
            int farBottomY = scaledHeight - 10;

            int ah = playerEntity.getMaxAir();
            int ai = Math.min(playerEntity.getAir(), ah);

            if (playerEntity.isSubmergedIn(FluidTags.WATER) || ai < ah)
                rightY -= 10; // Make room for water bubbles

            int toughnessPosX = getCorrectCoordinates(toughnessConfig, leftX, rightX, farLeftX, farRightX, farLeftX, farMiddleRightX, farLeftX, farRightX);
            int toughnessPosY = getCorrectCoordinates(toughnessConfig, leftY, rightY, 1, 1, farMiddleY, farMiddleY, farBottomY, farBottomY);
            int knockbackPosX = getCorrectCoordinates(knockbackConfig, leftX, rightX, farLeftX, farRightX, farLeftX, farMiddleRightX, farLeftX, farRightX);
            int knockbackPosY = getCorrectCoordinates(knockbackConfig, leftY, rightY, 1, 1, farMiddleY, farMiddleY, farBottomY, farBottomY);

            // Move knockback resistance bar to leave space for toughness bar if they are both displayed in the same part of the screen
            if (toughnessConfig.side == knockbackConfig.side && toughnessConfig.position == knockbackConfig.position && toughnessConfig.enabled)
                if (toughnessConfig.position == PositionDisplayOption.TOP) toughnessPosY += 10;
                else if (knockbackConfig.position == PositionDisplayOption.MIDDLE)
                    knockbackPosX += knockbackConfig.side == SideDisplayOption.LEFT ? 10 : -10;
                else knockbackPosY -= 10;

            // use custom icons texture
            RenderSystem.setShaderTexture(0, EXTRA_ARMOUR_INFO_ICONS_TEXTURE);
            this.client.getProfiler().swap("extra-armour-info");

            double armourToughness = 0;
            double knockbackResistance = 0;
            for (ItemStack armorItem : playerEntity.getArmorItems())
                if (armorItem.getItem() instanceof ArmorItem) {
                    armourToughness += ((ArmorItem) armorItem.getItem()).getMaterial().getToughness();
                    knockbackResistance += ((ArmorItem) armorItem.getItem()).getMaterial().getKnockbackResistance();
                }

            double v = Math.floor(armourToughness);
            double v2 = Math.floor(knockbackResistance * 10);

            drawStatusBarIcons(toughnessConfig, matrices, toughnessPosX, toughnessPosY, 0, v);
            drawStatusBarIcons(knockbackConfig, matrices, knockbackPosX, knockbackPosY, 9, v2);

            // Revert to GUI icons once done
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        }
    }

    private void drawStatusBarIcons(SubCategoryConfig config, MatrixStack matrices, int x, int y, int textureV, double v) {
        // Tweak the X and Y by custom config positions
        int x2 = x + config.getTweakX();
        int y2 = y + config.getTweakY();
        if (config.isEnabled()) {
            // Draw each icon in the status bar
            boolean isMiddle = config.getPosition() == PositionDisplayOption.MIDDLE;
            boolean isLeft = config.getSide() == SideDisplayOption.LEFT;
            for (int i = 0; i < 10; ++i) {
                if (v > 0) {
                    int offset = isMiddle ? i * 9 : (isLeft ? i * 8 : 9 * 8 - i * 8);
                    int screenX = isMiddle ? x2 : x2 + offset;
                    int screenY = isMiddle ? y2 + offset : y2;
                    if (i * 2 + 1 < v)
                        // Draw the fully filled icon
                        drawCustomTexture(matrices, screenX, screenY, 18, textureV, 9, 9);
                    else if (i * 2 + 1 == v) {
                        // Draw half the empty icon and the other half of the split icon
                        int tweakSide = (isLeft ? 1 : 0) * 4;
                        drawCustomTexture(matrices, screenX + tweakSide, screenY, tweakSide, textureV, 5, 9);
                        drawCustomTexture(matrices, screenX + 4 - tweakSide, screenY, 9 + 4 - tweakSide, textureV, 5, 9);
                    } else if (i * 2 + 1 > v)
                        // Draw the empty icon
                        drawCustomTexture(matrices, screenX, screenY, 0, textureV, 9, 9);
                }
            }
        }
    }

    private int getCorrectCoordinates(SubCategoryConfig config, int hudLeft, int hudRight, int topLeftY, int topRightY, int middleLeftY, int middleRightY, int bottomLeftY, int bottomRightY) {
        boolean isLeft = config.getSide() == SideDisplayOption.LEFT;
        return switch (config.getPosition()) {
            case HUD -> isLeft ? hudLeft : hudRight;
            case TOP -> isLeft ? topLeftY : topRightY;
            case MIDDLE -> isLeft ? middleLeftY : middleRightY;
            case BOTTOM -> isLeft ? bottomLeftY : bottomRightY;
        };
    }

    public void drawCustomTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, 0, (float) u, (float) v, width, height, 32, 32);
    }

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        ExtraArmourInfo extraArmourInfo = ExtraArmourInfo.getInstance();
        HotIconConfig hotIconConfig = extraArmourInfo.getHotIconConfig();

        if (hotIconConfig.isEnabled()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            PlayerEntity playerEntity = getCameraPlayer();
            PlayerInventory inventory = playerEntity.getInventory();

            boolean isLeft = hotIconConfig.getSide() == SideDisplayOption.LEFT;
            boolean isBottom = hotIconConfig.getPosition() == PositionDisplayOption.BOTTOM;

            int o = isLeft ? 0 : scaledWidth;
            int p = 0;

            if (hotIconConfig.getPosition() == PositionDisplayOption.HUD) {
                o = scaledWidth / 2;
                o += isLeft ? -(91 + 29 + 16 + 8) : 91 + 8;
                p = scaledHeight - 4 * 16;
                o += hotIconConfig.getTweakX();
                p += hotIconConfig.getTweakY();
                for (int i = 0; i < 4; i++)
                    renderHotbarItem(o, p + i * 16, tickDelta, playerEntity, inventory.armor.get(3 - i), i + 1);
            } else if (hotIconConfig.getPosition() == PositionDisplayOption.MIDDLE) {
                p = scaledHeight / 2 - 32;
                if (!isLeft) o -= 16;
                o += hotIconConfig.getTweakX();
                p += hotIconConfig.getTweakY();
                for (int i = 0; i < 4; i++)
                    renderHotbarItem(o, p + i * 16, tickDelta, playerEntity, inventory.armor.get(3 - i), i + 1);
            } else {
                if (!isLeft) o -= 4 * 16;
                if (isBottom) p = scaledHeight - 16;
                o += hotIconConfig.getTweakX();
                p += hotIconConfig.getTweakY();
                for (int i = 0; i < 4; i++)
                    renderHotbarItem(o + i * 16, p, tickDelta, playerEntity, inventory.armor.get(3 - i), i + 1);

            }
            RenderSystem.disableBlend();
        }
    }
}
