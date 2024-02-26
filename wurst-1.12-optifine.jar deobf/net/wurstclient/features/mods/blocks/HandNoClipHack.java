// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.blocks;

import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.wurstclient.settings.BlockListSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "hand noclip", "hand no clip", "GhostHand", "ghost hand" })
@Bypasses(ghostMode = false)
public final class HandNoClipHack extends Hack
{
    private final BlockListSetting blocks;
    
    public HandNoClipHack() {
        super("HandNoClip", "Allows you to reach specific blocks through walls.");
        this.blocks = new BlockListSetting("Blocks", "The blocks you want to reach through walls.", new String[] { "minecraft:black_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:chest", "minecraft:cyan_shulker_box", "minecraft:dispenser", "minecraft:dropper", "minecraft:ender_chest", "minecraft:gray_shulker_box", "minecraft:green_shulker_box", "minecraft:hopper", "minecraft:light_blue_shulker_box", "minecraft:lime_shulker_box", "minecraft:magenta_shulker_box", "minecraft:orange_shulker_box", "minecraft:pink_shulker_box", "minecraft:purple_shulker_box", "minecraft:red_shulker_box", "minecraft:trapped_chest", "minecraft:white_shulker_box", "minecraft:yellow_shulker_box" });
        this.setCategory(Category.BLOCKS);
        this.addSetting(this.blocks);
    }
    
    public boolean isBlockInList(final String name) {
        return this.blocks.getBlockNames().contains(name);
    }
}
