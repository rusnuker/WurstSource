// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.wurstclient.compatibility.WItem;
import net.minecraft.inventory.Slot;
import net.minecraft.client.gui.GuiButton;
import net.wurstclient.WurstClient;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.client.Minecraft;
import net.wurstclient.features.mods.items.AutoStealMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer
{
    private static final ResourceLocation CHEST_GUI_TEXTURE;
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;
    private final AutoStealMod autoSteal;
    private int mode;
    private final int inventoryRows;
    
    static {
        CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    }
    
    public GuiChest(final IInventory upperInv, final IInventory lowerInv) {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
        this.autoSteal = WurstClient.INSTANCE.hax.autoStealMod;
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        final int i = 222;
        final int j = 114;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        if (this.autoSteal.areButtonsVisible()) {
            this.buttonList.add(new GuiButton(0, this.guiLeft + this.xSize - 108, this.guiTop + 4, 50, 12, "Steal"));
            this.buttonList.add(new GuiButton(1, this.guiLeft + this.xSize - 56, this.guiTop + 4, 50, 12, "Store"));
        }
        if (this.autoSteal.isActive()) {
            this.steal();
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        switch (button.id) {
            case 0: {
                this.steal();
                break;
            }
            case 1: {
                this.store();
                break;
            }
        }
    }
    
    private void steal() {
        this.runInThread(() -> this.shiftClickSlots(0, this.inventoryRows * 9, 1));
    }
    
    private void store() {
        this.runInThread(() -> this.shiftClickSlots(this.inventoryRows * 9, this.inventoryRows * 9 + 44, 2));
    }
    
    private void runInThread(final Runnable r) {
        new Thread(() -> {
            try {
                runnable.run();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void shiftClickSlots(final int from, final int to, final int mode) {
        this.mode = mode;
        for (int i = from; i < to; ++i) {
            final Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (!WItem.isNullOrEmpty(slot.getStack())) {
                this.waitForDelay();
                if (this.mode != mode) {
                    break;
                }
                if (this.mc.currentScreen == null) {
                    break;
                }
                this.handleMouseClick(slot, slot.slotNumber, 0, ClickType.QUICK_MOVE);
            }
        }
    }
    
    private void waitForDelay() {
        try {
            Thread.sleep(this.autoSteal.getDelay());
        }
        catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiChest.CHEST_GUI_TEXTURE);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
