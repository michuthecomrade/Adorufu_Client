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
import com.sasha.adorufu.mod.events.client.ClientMouseClickEvent;
import com.sasha.adorufu.mod.feature.AbstractAdorufuTogglableFeature;
import com.sasha.adorufu.mod.feature.AdorufuCategory;
import com.sasha.adorufu.mod.feature.annotation.FeatureInfo;
import com.sasha.adorufu.mod.misc.Manager;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;

@FeatureInfo(description = "Middle click a block to add it to xray")
public class MiddleClickBlockFeature extends AbstractAdorufuTogglableFeature implements SimpleListener {
    public MiddleClickBlockFeature() {
        super("MiddleClickBlock", AdorufuCategory.MISC);
    }

    private static void refreshXray() {
        if (Manager.Feature.isFeatureEnabled(XrayFeature.class)) {
            AdorufuMod.minecraft.renderGlobal.loadRenderers();
        }
    }

    @SimpleEventHandler
    public void onMiddleClick(ClientMouseClickEvent.Middle e) {
        if (AdorufuMod.minecraft.world.getBlockState(AdorufuMod.minecraft.objectMouseOver.getBlockPos()).getBlock().material == Material.AIR) {
            return;
        }
        BlockPos en = AdorufuMod.minecraft.objectMouseOver.getBlockPos();
        Block b = AdorufuMod.minecraft.world.getBlockState(en).getBlock();
        if (!XrayFeature.isVisibleInXray(b)) {
            XrayFeature.xRayBlocks.add(Block.getIdFromBlock(b));
            AdorufuMod.logMsg(false, b.getLocalizedName() + " added.");
            refreshXray();
            return;
        }
        XrayFeature.xRayBlocks.remove(Block.getIdFromBlock(b));
        refreshXray();
        AdorufuMod.logMsg(false, b.getLocalizedName() + " removed.");
    }
}
