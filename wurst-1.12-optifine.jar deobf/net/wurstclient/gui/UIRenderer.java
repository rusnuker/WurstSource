// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui;

import net.wurstclient.clickgui.ClickGui;
import net.wurstclient.events.GUIRenderListener;
import net.minecraft.client.Minecraft;
import net.wurstclient.clickgui.screens.ClickGuiScreen;
import org.lwjgl.opengl.GL11;
import net.wurstclient.WurstClient;

public final class UIRenderer
{
    private static final WurstLogo wurstLogo;
    public static final HackList modList;
    private static final TabGui tabGui;
    
    static {
        wurstLogo = new WurstLogo();
        modList = new HackList();
        tabGui = new TabGui();
    }
    
    public static void renderUI(final float partialTicks) {
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        final boolean blend = GL11.glGetBoolean(3042);
        final ClickGui clickGui = WurstClient.INSTANCE.getGui();
        GL11.glDisable(2884);
        GL11.glDisable(3553);
        clickGui.updateColors();
        UIRenderer.wurstLogo.render();
        UIRenderer.modList.render(partialTicks);
        UIRenderer.tabGui.render(partialTicks);
        if (!(Minecraft.getMinecraft().currentScreen instanceof ClickGuiScreen)) {
            clickGui.renderPinnedWindows(partialTicks);
        }
        WurstClient.INSTANCE.events.fire(GUIRenderListener.GUIRenderEvent.INSTANCE);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (blend) {
            GL11.glEnable(3042);
        }
        else {
            GL11.glDisable(3042);
        }
    }
}
