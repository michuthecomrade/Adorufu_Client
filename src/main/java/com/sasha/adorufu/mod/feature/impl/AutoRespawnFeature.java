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
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Sasha on 11/08/2018 at 6:32 PM
 **/
@FeatureInfo(description = "Automatically respawn upon death")
public class AutoRespawnFeature extends AbstractAdorufuTogglableFeature implements IAdorufuTickableFeature {

    public AutoRespawnFeature() {
        super("AutoRespawn", AdorufuCategory.COMBAT);
    }

    @Override
    public void onTick() {
        if (AdorufuMod.minecraft.currentScreen instanceof GuiGameOver) {
            AdorufuMod.minecraft.player.respawnPlayer();
            AdorufuMod.minecraft.displayGuiScreen((GuiScreen) null);
        }
    }
}
