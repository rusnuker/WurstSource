// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.clickgui.screens.EditBlockListScreen;
import net.minecraft.client.gui.GuiScreen;
import java.util.List;
import java.util.Collections;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.block.Block;
import java.util.Collection;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import java.util.ArrayList;
import net.wurstclient.settings.BlockListSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderTileEntityListener;
import net.wurstclient.events.RenderBlockModelListener;
import net.wurstclient.events.ShouldSideBeRenderedListener;
import net.wurstclient.events.GetAmbientOcclusionLightValueListener;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "XRay", "x ray", "OreFinder", "ore finder" })
@Bypasses
public final class XRayMod extends Hack implements UpdateListener, SetOpaqueCubeListener, GetAmbientOcclusionLightValueListener, ShouldSideBeRenderedListener, RenderBlockModelListener, RenderTileEntityListener
{
    private final BlockListSetting blocks;
    private ArrayList<String> blockNames;
    
    public XRayMod() {
        super("X-Ray", "Allows you to see ores through walls.");
        this.blocks = new BlockListSetting("Ores", "", new String[] { "minecraft:anvil", "minecraft:beacon", "minecraft:bone_block", "minecraft:bookshelf", "minecraft:brewing_stand", "minecraft:chain_command_block", "minecraft:chest", "minecraft:clay", "minecraft:coal_block", "minecraft:coal_ore", "minecraft:command_block", "minecraft:crafting_table", "minecraft:diamond_block", "minecraft:diamond_ore", "minecraft:dispenser", "minecraft:dropper", "minecraft:emerald_block", "minecraft:emerald_ore", "minecraft:enchanting_table", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:ender_chest", "minecraft:flowing_lava", "minecraft:flowing_water", "minecraft:furnace", "minecraft:glowstone", "minecraft:gold_block", "minecraft:gold_ore", "minecraft:hopper", "minecraft:iron_block", "minecraft:iron_ore", "minecraft:ladder", "minecraft:lapis_block", "minecraft:lapis_ore", "minecraft:lava", "minecraft:lit_furnace", "minecraft:lit_redstone_ore", "minecraft:mob_spawner", "minecraft:mossy_cobblestone", "minecraft:portal", "minecraft:quartz_ore", "minecraft:redstone_block", "minecraft:redstone_ore", "minecraft:repeating_command_block", "minecraft:tnt", "minecraft:torch", "minecraft:trapped_chest", "minecraft:water" });
        this.setCategory(Category.RENDER);
        this.addSetting(this.blocks);
    }
    
    @Override
    public String getRenderName() {
        return "X-Wurst";
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { XRayMod.WURST.commands.xRayCmd };
    }
    
    @Override
    public void onEnable() {
        this.blockNames = new ArrayList<String>(this.blocks.getBlockNames());
        XRayMod.EVENTS.add(UpdateListener.class, this);
        XRayMod.EVENTS.add(SetOpaqueCubeListener.class, this);
        XRayMod.EVENTS.add(GetAmbientOcclusionLightValueListener.class, this);
        XRayMod.EVENTS.add(ShouldSideBeRenderedListener.class, this);
        XRayMod.EVENTS.add(RenderBlockModelListener.class, this);
        XRayMod.EVENTS.add(RenderTileEntityListener.class, this);
        XRayMod.MC.renderGlobal.loadRenderers();
    }
    
    @Override
    public void onDisable() {
        XRayMod.EVENTS.remove(UpdateListener.class, this);
        XRayMod.EVENTS.remove(SetOpaqueCubeListener.class, this);
        XRayMod.EVENTS.remove(GetAmbientOcclusionLightValueListener.class, this);
        XRayMod.EVENTS.remove(ShouldSideBeRenderedListener.class, this);
        XRayMod.EVENTS.remove(RenderBlockModelListener.class, this);
        XRayMod.EVENTS.remove(RenderTileEntityListener.class, this);
        XRayMod.MC.renderGlobal.loadRenderers();
        if (!XRayMod.WURST.hax.fullbrightMod.isActive()) {
            XRayMod.MC.gameSettings.gammaSetting = 0.5f;
        }
    }
    
    @Override
    public void onUpdate() {
        XRayMod.MC.gameSettings.gammaSetting = 16.0f;
    }
    
    @Override
    public void onSetOpaqueCube(final SetOpaqueCubeEvent event) {
        event.cancel();
    }
    
    @Override
    public void onGetAmbientOcclusionLightValue(final GetAmbientOcclusionLightValueEvent event) {
        event.setLightValue(1.0f);
    }
    
    @Override
    public void onShouldSideBeRendered(final ShouldSideBeRenderedEvent event) {
        event.setRendered(this.isVisible(event.getState().getBlock()));
    }
    
    @Override
    public void onRenderBlockModel(final RenderBlockModelEvent event) {
        if (!this.isVisible(event.getState().getBlock())) {
            event.cancel();
        }
    }
    
    @Override
    public void onRenderTileEntity(final RenderTileEntityEvent event) {
        if (!this.isVisible(event.getTileEntity().getBlockType())) {
            event.cancel();
        }
    }
    
    private boolean isVisible(final Block block) {
        final String name = WBlock.getName(block);
        final int index = Collections.binarySearch(this.blockNames, name);
        return index >= 0;
    }
    
    public List<String> getBlockNames() {
        return this.blocks.getBlockNames();
    }
    
    public int getIndex(final String blockName) {
        return Collections.binarySearch(this.blocks.getBlockNames(), blockName);
    }
    
    public void addBlock(final Block block) {
        this.blocks.add(block);
    }
    
    public void removeBlock(final int index) {
        this.blocks.remove(index);
    }
    
    public void resetBlocks() {
        this.blocks.resetToDefaults();
    }
    
    public void showBlockListEditor(final GuiScreen prevScreen) {
        XRayMod.MC.openScreen(new EditBlockListScreen(prevScreen, this.blocks));
    }
}
