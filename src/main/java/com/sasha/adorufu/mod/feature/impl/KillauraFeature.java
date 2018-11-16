/*
 * Copyright (c) Sasha Stevens 2018.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.sasha.adorufu.mod.feature.impl;

import com.sasha.adorufu.mod.AdorufuMod;
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.IAdorufuTickableFeature;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.feature.option.AdorufuFeatureOption;
import com.sasha.simplesettings.annotation.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sasha.adorufu.mod.AdorufuMod.FRIEND_MANAGER;
import static com.sasha.adorufu.mod.AdorufuMod.minecraft;

/**
 * Created by Sasha on 08/08/2018 at 12:23 PM
 **/
@FeatureInfo(description = "Automatically attacks nearby entities, excluding friended players.")
public class KillauraFeature extends AbstractAdorufuTogglableFeature implements IAdorufuTickableFeature {

    @Setting public double range = 5.0;

    public KillauraFeature() {
        super("KillAura", AdorufuCategory.COMBAT,
                new AdorufuFeatureOption<>("Passives", false),
                new AdorufuFeatureOption<>("Players", true),
                new AdorufuFeatureOption<>("Hostiles", true),
                new AdorufuFeatureOption<>("Friends", false),
                new AdorufuFeatureOption<>("Auto Sword Switch", false));
    }

    public static void rotateTowardsEntity(Entity entity) {
        double x = entity.posX - minecraft.player.posX;
        double z = entity.posZ - minecraft.player.posZ;
        double y = entity.posY + entity.getEyeHeight() / 1.4D - minecraft.player.posY/* + minecraft.player.getEyeHeight() / 1.4D**/;
        double rotatesq = MathHelper.sqrt(x * x + z * z);
        float newYaw = (float) Math.toDegrees(-Math.atan(x / z));
        float newPitch = (float) -Math.toDegrees(Math.atan(y / rotatesq) - 0.8f);
        if ((z < 0.0D) && (x < 0.0D)) {
            newYaw = (float) (90.0D + Math.toDegrees(Math.atan(z / x)));
        } else if ((z < 0.0D) && (x > 0.0D)) {
            newYaw = (float) (-90.0D + Math.toDegrees(Math.atan(z / x)));
        }
        minecraft.player.rotationYaw = newYaw;
        minecraft.player.rotationPitch = newPitch;
        minecraft.player.rotationYawHead = newPitch;
    }

    @Override
    public void onTick() {
        this.setSuffix(this.getFormattableOptionsMap());
        if (minecraft.player.isHandActive()) return; // Return if eating / holding up a shield / ...
        if (minecraft.player.getCooledAttackStrength(1) < 1) return;
        List<EntityLivingBase> entityLivingBase = minecraft.world.loadedEntityList.stream()
                .filter(e ->
                        e instanceof EntityLivingBase // Is this entity living?
                                && !(e instanceof EntityPlayerSP)   // Is this entity not the local player?
                                && minecraft.player.getDistance(e) <= 5.0f // Is this entity closer than 5 blocks?
                                && e.isEntityAlive()    // Is this entity alive?
                                && ((EntityLivingBase) e).hurtTime == 0 // Has this entity not been hurt recently?
                                && (!(e instanceof EntityPlayer) || !AdorufuMod.FRIEND_MANAGER.isFriended(e.getName())) // Is this entity a player? If so, are they not friended?
                ).map(entity -> (EntityLivingBase) entity).collect(Collectors.toList());
        if (entityLivingBase.isEmpty()) return;
        Optional<EntityLivingBase> bb = entityLivingBase.stream().findFirst();
        EntityLivingBase b = bb.get();
        if (b instanceof EntityAnimal && !this.getOption("Passives")) return;
        if (b instanceof EntityOtherPlayerMP && !this.getOption("Players")) return;
        if (b instanceof EntityMob && !this.getOption("Hostiles")) return;
        if (b instanceof EntityOtherPlayerMP && FRIEND_MANAGER.isFriended(b.getName())) return;
        if (this.getOption("Auto Sword Switch")) {
            for (int s = 0; s <= 8; s++) {
                ItemStack stack = AdorufuMod.minecraft.player.inventory.getStackInSlot(s);
                if (stack.getItem() == Items.DIAMOND_SWORD) {
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    break;
                }
                if (stack.getItem() == Items.IRON_SWORD) {
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    break;
                }
                if (stack.getItem() == Items.STONE_SWORD) {
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    break;
                }
                if (stack.getItem() == Items.WOODEN_SWORD) {
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    break;
                }
                if (stack.getItem() == Items.GOLDEN_SWORD) {
                    AdorufuMod.minecraft.player.inventory.currentItem = s;
                    break;
                }
            }
            float yaw = minecraft.player.rotationYaw;
            float pitch = minecraft.player.rotationPitch;
            float yawh = minecraft.player.rotationYawHead;
            boolean wasSprinting = minecraft.player.isSprinting();
            rotateTowardsEntity(b);
            minecraft.player.setSprinting(false);
            minecraft.playerController.attackEntity(minecraft.player, b);
            minecraft.player.swingArm(EnumHand.MAIN_HAND);
            minecraft.player.rotationYaw = yaw;
            minecraft.player.rotationPitch = pitch;
            minecraft.player.rotationYawHead = yawh;
            if (wasSprinting)
                minecraft.player.setSprinting(true);
        }
    }
}
