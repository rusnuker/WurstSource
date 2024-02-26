// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.other;

import net.minecraft.entity.projectile.EntityFishHook;
import net.wurstclient.compatibility.WSoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.wurstclient.compatibility.WEnchantments;
import net.minecraft.item.ItemFishingRod;
import net.wurstclient.compatibility.WItem;
import net.minecraft.item.ItemStack;
import net.wurstclient.util.RenderUtils;
import net.minecraft.util.math.Box;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.compatibility.WPlayerController;
import org.lwjgl.opengl.GL11;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.util.math.Vec3d;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "FishBot", "auto fish", "fish bot", "fishing" })
@Bypasses
public final class AutoFishMod extends Hack implements UpdateListener, PacketInputListener, RenderListener
{
    private final SliderSetting validRange;
    private CheckboxSetting debugDraw;
    private int bestRodValue;
    private int bestRodSlot;
    private int castRodTimer;
    private int reelInTimer;
    private int box;
    private int cross;
    private int scheduledWindowClick;
    private Vec3d lastSoundPos;
    
    public AutoFishMod() {
        super("AutoFish", "Automatically catches fish using your\nbest fishing rod. If it finds a better\nrod while fishing, it will automatically\nswitch to it.");
        this.validRange = new SliderSetting("Valid range", "Any bites that occur outside of this range\nwill be ignored.\n\nIncrease your range if bites are not being\ndetected, decrease it if other people's\nbites are being detected as yours.", 1.5, 0.25, 8.0, 0.25, SliderSetting.ValueDisplay.DECIMAL);
        this.debugDraw = new CheckboxSetting("Debug draw", "Shows where bites are occurring and where\nthey will be detected. Useful for optimizing\nyour 'Valid range' setting.", false);
        this.setCategory(Category.OTHER);
        this.addSetting(this.validRange);
        this.addSetting(this.debugDraw);
    }
    
    @Override
    public void onEnable() {
        this.bestRodValue = -1;
        this.bestRodSlot = -1;
        this.castRodTimer = 0;
        this.reelInTimer = -1;
        this.scheduledWindowClick = -1;
        this.lastSoundPos = null;
        this.box = GL11.glGenLists(1);
        GL11.glNewList(this.cross = GL11.glGenLists(1), 4864);
        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
        GL11.glBegin(1);
        GL11.glVertex3d(-0.125, 0.0, -0.125);
        GL11.glVertex3d(0.125, 0.0, 0.125);
        GL11.glVertex3d(0.125, 0.0, -0.125);
        GL11.glVertex3d(-0.125, 0.0, 0.125);
        GL11.glEnd();
        GL11.glEndList();
        AutoFishMod.EVENTS.add(UpdateListener.class, this);
        AutoFishMod.EVENTS.add(PacketInputListener.class, this);
        AutoFishMod.EVENTS.add(RenderListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AutoFishMod.EVENTS.remove(UpdateListener.class, this);
        AutoFishMod.EVENTS.remove(PacketInputListener.class, this);
        AutoFishMod.EVENTS.remove(RenderListener.class, this);
        GL11.glDeleteLists(this.box, 1);
        GL11.glDeleteLists(this.cross, 1);
    }
    
    @Override
    public void onUpdate() {
        this.updateDebugDraw();
        if (this.reelInTimer > 0) {
            --this.reelInTimer;
        }
        final EntityPlayerSP player = AutoFishMod.MC.player;
        final InventoryPlayer inventory = player.inventory;
        if (this.scheduledWindowClick != -1) {
            WPlayerController.windowClick_PICKUP(this.scheduledWindowClick);
            this.castRodTimer = 15;
            return;
        }
        this.updateBestRod();
        if (this.bestRodSlot == -1) {
            ChatUtils.message("Out of fishing rods.");
            this.setEnabled(false);
            return;
        }
        if (this.bestRodSlot != inventory.selectedSlot) {
            this.selectBestRod();
            return;
        }
        if (this.castRodTimer > 0) {
            --this.castRodTimer;
            return;
        }
        if (player.fishEntity == null) {
            this.rightClick();
            this.castRodTimer = 15;
            this.reelInTimer = 1200;
        }
        if (this.reelInTimer == 0) {
            --this.reelInTimer;
            this.rightClick();
            this.castRodTimer = 15;
        }
    }
    
    private void updateDebugDraw() {
        if (this.debugDraw.isChecked()) {
            GL11.glNewList(this.box, 4864);
            final Box box = new Box(-this.validRange.getValue(), -0.0625, -this.validRange.getValue(), this.validRange.getValue(), 0.0625, this.validRange.getValue());
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);
            RenderUtils.drawOutlinedBox(box);
            GL11.glEndList();
        }
    }
    
    private void updateBestRod() {
        final InventoryPlayer inventory = AutoFishMod.MC.player.inventory;
        final int selectedSlot = inventory.selectedSlot;
        final ItemStack selectedStack = inventory.getInvStack(selectedSlot);
        this.bestRodValue = this.getRodValue(selectedStack);
        this.bestRodSlot = ((this.bestRodValue > -1) ? selectedSlot : -1);
        for (int slot = 0; slot < 36; ++slot) {
            final ItemStack stack = inventory.getInvStack(slot);
            final int rodValue = this.getRodValue(stack);
            if (rodValue > this.bestRodValue) {
                this.bestRodValue = rodValue;
                this.bestRodSlot = slot;
            }
        }
    }
    
    private int getRodValue(final ItemStack stack) {
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemFishingRod)) {
            return -1;
        }
        final int luckOTSLvl = WEnchantments.getEnchantmentLevel(WEnchantments.LUCK_OF_THE_SEA, stack);
        final int lureLvl = WEnchantments.getEnchantmentLevel(WEnchantments.LURE, stack);
        final int unbreakingLvl = WEnchantments.getEnchantmentLevel(WEnchantments.UNBREAKING, stack);
        final int mendingBonus = WEnchantments.getEnchantmentLevel(WEnchantments.MENDING, stack);
        final int noVanishBonus = WEnchantments.hasVanishingCurse(stack) ? 0 : 1;
        return luckOTSLvl * 9 + lureLvl * 9 + unbreakingLvl * 2 + mendingBonus + noVanishBonus;
    }
    
    private void selectBestRod() {
        final InventoryPlayer inventory = AutoFishMod.MC.player.inventory;
        if (this.bestRodSlot < 9) {
            inventory.selectedSlot = this.bestRodSlot;
            return;
        }
        final int firstEmptySlot = inventory.getFirstEmptyStack();
        if (firstEmptySlot != -1) {
            if (firstEmptySlot >= 9) {
                WPlayerController.windowClick_QUICK_MOVE(36 + inventory.selectedSlot);
            }
            WPlayerController.windowClick_QUICK_MOVE(this.bestRodSlot);
        }
        else {
            WPlayerController.windowClick_PICKUP(this.bestRodSlot);
            WPlayerController.windowClick_PICKUP(36 + inventory.selectedSlot);
            this.scheduledWindowClick = -this.bestRodSlot;
        }
    }
    
    @Override
    public void onReceivedPacket(final PacketInputEvent event) {
        final EntityPlayerSP player = AutoFishMod.MC.player;
        if (player == null || player.fishEntity == null) {
            return;
        }
        if (!(event.getPacket() instanceof SPacketSoundEffect)) {
            return;
        }
        final SPacketSoundEffect sound = (SPacketSoundEffect)event.getPacket();
        if (!WSoundEvents.isBobberSplash(sound)) {
            return;
        }
        if (this.debugDraw.isChecked()) {
            this.lastSoundPos = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
        }
        final EntityFishHook bobber = player.fishEntity;
        if (Math.abs(sound.getX() - bobber.posX) > this.validRange.getValue() || Math.abs(sound.getZ() - bobber.posZ) > this.validRange.getValue()) {
            return;
        }
        this.rightClick();
    }
    
    private void rightClick() {
        final ItemStack stack = AutoFishMod.MC.player.inventory.getCurrentItem();
        if (WItem.isNullOrEmpty(stack) || !(stack.getItem() instanceof ItemFishingRod)) {
            return;
        }
        AutoFishMod.MC.rightClickMouse();
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (!this.debugDraw.isChecked()) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        RenderUtils.applyRenderOffset();
        final EntityFishHook bobber = AutoFishMod.MC.player.fishEntity;
        if (bobber != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(bobber.posX, bobber.posY, bobber.posZ);
            GL11.glCallList(this.box);
            GL11.glPopMatrix();
        }
        if (this.lastSoundPos != null) {
            GL11.glPushMatrix();
            GL11.glTranslated(this.lastSoundPos.xCoord, this.lastSoundPos.yCoord, this.lastSoundPos.zCoord);
            GL11.glCallList(this.cross);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
