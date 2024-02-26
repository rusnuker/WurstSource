// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import com.google.gson.JsonPrimitive;
import net.wurstclient.util.json.JsonException;
import net.wurstclient.util.json.JsonUtils;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.BlockComponent;
import net.wurstclient.clickgui.Component;
import net.wurstclient.WurstClient;
import net.minecraft.block.BlockAir;
import net.minecraft.block.Block;
import java.util.Objects;
import net.wurstclient.util.BlockUtils;

public final class BlockSetting extends Setting
{
    private String blockName;
    private final String defaultName;
    
    public BlockSetting(final String name, final String description, final String blockName) {
        super(name, description);
        this.blockName = "";
        final Block block = BlockUtils.getBlockFromName(blockName);
        Objects.requireNonNull(block);
        this.blockName = BlockUtils.getName(block);
        this.defaultName = this.blockName;
    }
    
    public BlockSetting(final String name, final String blockName) {
        this(name, "", blockName);
    }
    
    public Block getBlock() {
        return BlockUtils.getBlockFromName(this.blockName);
    }
    
    public String getBlockName() {
        return this.blockName;
    }
    
    public void setBlock(final Block block) {
        if (block == null || block instanceof BlockAir) {
            return;
        }
        final String newName = Objects.requireNonNull(BlockUtils.getName(block));
        if (this.blockName.equals(newName)) {
            return;
        }
        this.blockName = newName;
        WurstClient.INSTANCE.saveSettings();
    }
    
    public void setBlockName(final String blockName) {
        final Block block = BlockUtils.getBlockFromName(blockName);
        Objects.requireNonNull(block);
        this.setBlock(block);
    }
    
    public void resetToDefault() {
        this.blockName = this.defaultName;
        WurstClient.INSTANCE.saveSettings();
    }
    
    @Override
    public Component getComponent() {
        return new BlockComponent(this);
    }
    
    @Override
    public void fromJson(final JsonElement json) {
        try {
            final String newName = JsonUtils.getAsString(json);
            final Block newBlock = BlockUtils.getBlockFromName(newName);
            if (newBlock == null || newBlock instanceof BlockAir) {
                throw new JsonException();
            }
            this.blockName = BlockUtils.getName(newBlock);
        }
        catch (final JsonException e) {
            e.printStackTrace();
            this.resetToDefault();
        }
    }
    
    @Override
    public JsonElement toJson() {
        return (JsonElement)new JsonPrimitive(this.blockName);
    }
    
    @Override
    public ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        return new ArrayList<PossibleKeybind>();
    }
}
