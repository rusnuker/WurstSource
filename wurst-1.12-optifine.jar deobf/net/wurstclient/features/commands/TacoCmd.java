// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.WurstClient;
import org.lwjgl.opengl.GL11;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.Category;
import net.minecraft.util.ResourceLocation;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/taco")
public final class TacoCmd extends Command implements GUIRenderListener, UpdateListener
{
    private final ResourceLocation[] tacos;
    private boolean enabled;
    private int ticks;
    
    public TacoCmd() {
        super("taco", "Spawns a dancing taco on your hotbar.\n\"I love that little guy. So cute!\" -WiZARD", new String[0]);
        this.tacos = new ResourceLocation[] { new ResourceLocation("wurst/dancingtaco1.png"), new ResourceLocation("wurst/dancingtaco2.png"), new ResourceLocation("wurst/dancingtaco3.png"), new ResourceLocation("wurst/dancingtaco4.png") };
        this.ticks = 0;
        this.setCategory(Category.FUN);
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 0) {
            throw new CmdSyntaxError("Tacos don't need arguments!");
        }
        this.enabled = !this.enabled;
        if (this.enabled) {
            TacoCmd.EVENTS.add(GUIRenderListener.class, this);
            TacoCmd.EVENTS.add(UpdateListener.class, this);
        }
        else {
            TacoCmd.EVENTS.remove(GUIRenderListener.class, this);
            TacoCmd.EVENTS.remove(UpdateListener.class, this);
        }
    }
    
    @Override
    public String getPrimaryAction() {
        return "Be a BOSS!";
    }
    
    @Override
    public void doPrimaryAction() {
        TacoCmd.WURST.commands.onSentMessage(new ChatOutputListener.ChatOutputEvent(".taco", true));
    }
    
    @Override
    public void onRenderGUI() {
        GL11.glEnable(3042);
        GL11.glDisable(2884);
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 771);
        if (WurstClient.INSTANCE.hax.rainbowUiMod.isActive()) {
            final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
            GL11.glColor4f(acColor[0], acColor[1], acColor[2], 1.0f);
        }
        else {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        TacoCmd.MC.getTextureManager().bindTexture(this.tacos[this.ticks / 8]);
        final ScaledResolution sr = new ScaledResolution(TacoCmd.MC);
        final int x = sr.getScaledWidth() / 2 - 32 + 76;
        final int y = sr.getScaledHeight() - 32 - 19;
        final int w = 64;
        final int h = 32;
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, w, h, (float)w, (float)h);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
    }
    
    @Override
    public void onUpdate() {
        if (this.ticks >= 31) {
            this.ticks = 0;
        }
        else {
            ++this.ticks;
        }
    }
}
