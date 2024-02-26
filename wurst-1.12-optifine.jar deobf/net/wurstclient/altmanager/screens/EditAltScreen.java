// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import net.minecraft.client.gui.GuiScreen;
import net.wurstclient.altmanager.Alt;
import net.wurstclient.altmanager.AltManager;

public final class EditAltScreen extends AltEditorScreen
{
    private final AltManager altManager;
    private Alt editedAlt;
    
    public EditAltScreen(final GuiScreen prevScreen, final AltManager altManager, final Alt editedAlt) {
        super(prevScreen);
        this.altManager = altManager;
        this.editedAlt = editedAlt;
    }
    
    @Override
    protected String getTitle() {
        return "Edit Alt";
    }
    
    @Override
    protected String getDefaultEmail() {
        return this.editedAlt.getEmail();
    }
    
    @Override
    protected String getDefaultPassword() {
        return this.editedAlt.getPassword();
    }
    
    @Override
    protected String getDoneButtonText() {
        return "Save";
    }
    
    @Override
    protected void pressDoneButton() {
        this.altManager.edit(this.editedAlt, this.getEmail(), this.getPassword());
        this.mc.openScreen(this.prevScreen);
    }
}
