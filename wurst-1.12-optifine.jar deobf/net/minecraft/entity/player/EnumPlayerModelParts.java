// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.entity.player;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;

public enum EnumPlayerModelParts
{
    CAPE("CAPE", 0, 0, "cape"), 
    JACKET("JACKET", 1, 1, "jacket"), 
    LEFT_SLEEVE("LEFT_SLEEVE", 2, 2, "left_sleeve"), 
    RIGHT_SLEEVE("RIGHT_SLEEVE", 3, 3, "right_sleeve"), 
    LEFT_PANTS_LEG("LEFT_PANTS_LEG", 4, 4, "left_pants_leg"), 
    RIGHT_PANTS_LEG("RIGHT_PANTS_LEG", 5, 5, "right_pants_leg"), 
    HAT("HAT", 6, 6, "hat");
    
    private final int partId;
    private final int partMask;
    private final String partName;
    private final ITextComponent name;
    
    private EnumPlayerModelParts(final String name, final int ordinal, final int partIdIn, final String partNameIn) {
        this.partId = partIdIn;
        this.partMask = 1 << partIdIn;
        this.partName = partNameIn;
        this.name = new TextComponentTranslation("options.modelPart." + partNameIn, new Object[0]);
    }
    
    public int getPartMask() {
        return this.partMask;
    }
    
    public int getPartId() {
        return this.partId;
    }
    
    public String getPartName() {
        return this.partName;
    }
    
    public ITextComponent getName() {
        return this.name;
    }
}
