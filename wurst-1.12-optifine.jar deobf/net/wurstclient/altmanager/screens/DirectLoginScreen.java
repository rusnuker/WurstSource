// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager.screens;

import net.wurstclient.gui.main.GuiWurstMainMenu;
import net.wurstclient.altmanager.LoginManager;
import net.minecraft.client.gui.GuiScreen;

public final class DirectLoginScreen extends AltEditorScreen
{
    public DirectLoginScreen(final GuiScreen prevScreen) {
        super(prevScreen);
    }
    
    @Override
    protected String getTitle() {
        return "Direct Login";
    }
    
    @Override
    protected String getDoneButtonText() {
        return "Login";
    }
    
    @Override
    protected void pressDoneButton() {
        if (this.getPassword().isEmpty()) {
            this.message = "";
            LoginManager.changeCrackedName(this.getEmail());
        }
        else {
            this.message = LoginManager.login(this.getEmail(), this.getPassword());
        }
        if (this.message.isEmpty()) {
            this.mc.openScreen(new GuiWurstMainMenu());
        }
        else {
            this.doErrorEffect();
        }
    }
}
