// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.altmanager.AltManager;

public final class AddAltScreen extends AltEditorScreen
{
    private final AltManager altManager;
    
    public AddAltScreen(final GuiScreen prevScreen, final AltManager altManager) {
        super(prevScreen);
        this.altManager = altManager;
    }
    
    @Override
    protected String getTitle() {
        return "New Alt";
    }
    
    @Override
    protected String getDoneButtonText() {
        return "Add";
    }
    
    @Override
    protected void pressDoneButton() {
        this.altManager.add(this.getEmail(), this.getPassword(), false);
        this.mc.openScreen(this.prevScreen);
    }
}
