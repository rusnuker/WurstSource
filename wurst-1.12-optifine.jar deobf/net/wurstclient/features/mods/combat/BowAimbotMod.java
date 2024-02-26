// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.combat;

import java.util.function.ToDoubleFunction;
import net.minecraft.client.Minecraft;
import net.wurstclient.font.Fonts;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import java.util.Comparator;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.IMob;
import net.wurstclient.compatibility.WWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.EntityFakePlayer;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.wurstclient.util.RotationUtils;
import net.wurstclient.compatibility.WMinecraft;
import java.util.stream.Stream;
import net.minecraft.item.ItemBow;
import net.wurstclient.features.Feature;
import net.wurstclient.settings.Setting;
import net.wurstclient.Category;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.events.RenderListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Hack;

@SearchTags({ "bow aimbot" })
public final class BowAimbotMod extends Hack implements UpdateListener, RenderListener, GUIRenderListener
{
    private final EnumSetting<Priority> priority;
    private final SliderSetting predictMovement;
    private final CheckboxSetting filterPlayers;
    private final CheckboxSetting filterSleeping;
    private final SliderSetting filterFlying;
    private final CheckboxSetting filterMonsters;
    private final CheckboxSetting filterPigmen;
    private final CheckboxSetting filterEndermen;
    private final CheckboxSetting filterAnimals;
    private final CheckboxSetting filterBabies;
    private final CheckboxSetting filterPets;
    private final CheckboxSetting filterTraders;
    private final CheckboxSetting filterGolems;
    private final CheckboxSetting filterInvisible;
    private final CheckboxSetting filterNamed;
    private final CheckboxSetting filterStands;
    private final CheckboxSetting filterCrystals;
    private static final Box TARGET_BOX;
    private Entity target;
    private float velocity;
    
    static {
        TARGET_BOX = new Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
    }
    
    public BowAimbotMod() {
        super("BowAimbot", "Automatically aims your bow at the closest entity.\nTip: This works with FastBow.");
        this.priority = new EnumSetting<Priority>("Priority", "Determines which entity will be attacked first.\n§lDistance§r - Attacks the closest entity.\n§lAngle§r - Attacks the entity that requires\nthe least head movement.\n§lHealth§r - Attacks the weakest entity.", Priority.values(), Priority.ANGLE);
        this.predictMovement = new SliderSetting("Predict movement", "Controls the strength of BowAimbot's\nmovement prediction algorithm.", 0.2, 0.0, 2.0, 0.01, SliderSetting.ValueDisplay.PERCENTAGE);
        this.filterPlayers = new CheckboxSetting("Filter players", "Won't attack other players.", false);
        this.filterSleeping = new CheckboxSetting("Filter sleeping", "Won't attack sleeping players.", false);
        this.filterFlying = new SliderSetting("Filter flying", "Won't attack players that\nare at least the given\ndistance above ground.", 0.0, 0.0, 2.0, 0.05, v -> (v == 0.0) ? "off" : SliderSetting.ValueDisplay.DECIMAL.getValueString(v));
        this.filterMonsters = new CheckboxSetting("Filter monsters", "Won't attack zombies, creepers, etc.", false);
        this.filterPigmen = new CheckboxSetting("Filter pigmen", "Won't attack zombie pigmen.", false);
        this.filterEndermen = new CheckboxSetting("Filter endermen", "Won't attack endermen.", false);
        this.filterAnimals = new CheckboxSetting("Filter animals", "Won't attack pigs, cows, etc.", false);
        this.filterBabies = new CheckboxSetting("Filter babies", "Won't attack baby pigs,\nbaby villagers, etc.", false);
        this.filterPets = new CheckboxSetting("Filter pets", "Won't attack tamed wolves,\ntamed horses, etc.", false);
        this.filterTraders = new CheckboxSetting("Filter traders", "Won't attack villagers, wandering traders, etc.", false);
        this.filterGolems = new CheckboxSetting("Filter golems", "Won't attack iron golems,\nsnow golems and shulkers.", false);
        this.filterInvisible = new CheckboxSetting("Filter invisible", "Won't attack invisible entities.", false);
        this.filterNamed = new CheckboxSetting("Filter named", "Won't attack name-tagged entities.", false);
        this.filterStands = new CheckboxSetting("Filter armor stands", "Won't attack armor stands.", false);
        this.filterCrystals = new CheckboxSetting("Filter end crystals", "Won't attack end crystals.", false);
        this.setCategory(Category.COMBAT);
        this.addSetting(this.priority);
        this.addSetting(this.predictMovement);
        this.addSetting(this.filterPlayers);
        this.addSetting(this.filterSleeping);
        this.addSetting(this.filterFlying);
        this.addSetting(this.filterMonsters);
        this.addSetting(this.filterPigmen);
        this.addSetting(this.filterEndermen);
        this.addSetting(this.filterAnimals);
        this.addSetting(this.filterBabies);
        this.addSetting(this.filterPets);
        this.addSetting(this.filterTraders);
        this.addSetting(this.filterGolems);
        this.addSetting(this.filterInvisible);
        this.addSetting(this.filterNamed);
        this.addSetting(this.filterStands);
        this.addSetting(this.filterCrystals);
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { BowAimbotMod.WURST.hax.fastBowMod };
    }
    
    @Override
    public void onEnable() {
        BowAimbotMod.EVENTS.add(GUIRenderListener.class, this);
        BowAimbotMod.EVENTS.add(RenderListener.class, this);
        BowAimbotMod.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onDisable() {
        BowAimbotMod.EVENTS.remove(GUIRenderListener.class, this);
        BowAimbotMod.EVENTS.remove(RenderListener.class, this);
        BowAimbotMod.EVENTS.remove(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        if (!BowAimbotMod.MC.gameSettings.keyBindUseItem.pressed) {
            this.target = null;
            return;
        }
        if (!BowAimbotMod.MC.player.isHandActive() && !BowAimbotMod.WURST.hax.fastBowMod.isActive()) {
            this.target = null;
            return;
        }
        final ItemStack item = BowAimbotMod.MC.player.inventory.getCurrentItem();
        if (item == null || !(item.getItem() instanceof ItemBow)) {
            this.target = null;
            return;
        }
        if (this.filterEntities(Stream.of(this.target)) == null) {
            this.target = this.filterEntities(WMinecraft.getWorld().loadedEntityList.parallelStream());
        }
        if (this.target == null) {
            return;
        }
        this.velocity = (72000 - BowAimbotMod.MC.player.getItemInUseCount()) / 20.0f;
        this.velocity = (this.velocity * this.velocity + this.velocity * 2.0f) / 3.0f;
        if (this.velocity > 1.0f) {
            this.velocity = 1.0f;
        }
        if (BowAimbotMod.WURST.hax.fastBowMod.isActive()) {
            this.velocity = 1.0f;
        }
        final double d = RotationUtils.getEyesPos().distanceTo(this.target.boundingBox.getCenter()) * this.predictMovement.getValue();
        final double posX = this.target.posX + (this.target.posX - this.target.prevPosX) * d - BowAimbotMod.MC.player.posX;
        final double posY = this.target.posY + (this.target.posY - this.target.prevPosY) * d + this.target.height * 0.5 - BowAimbotMod.MC.player.posY - BowAimbotMod.MC.player.getEyeHeight();
        final double posZ = this.target.posZ + (this.target.posZ - this.target.prevPosZ) * d - BowAimbotMod.MC.player.posZ;
        BowAimbotMod.MC.player.rotationYaw = (float)Math.toDegrees(Math.atan2(posZ, posX)) - 90.0f;
        final double hDistance = Math.sqrt(posX * posX + posZ * posZ);
        final double hDistanceSq = hDistance * hDistance;
        final float g = 0.006f;
        final float velocitySq = this.velocity * this.velocity;
        final float velocityPow4 = velocitySq * velocitySq;
        final float neededPitch = (float)(-Math.toDegrees(Math.atan((velocitySq - Math.sqrt(velocityPow4 - g * (g * hDistanceSq + 2.0 * posY * velocitySq))) / (g * hDistance))));
        if (Float.isNaN(neededPitch)) {
            RotationUtils.faceEntityClient(this.target);
        }
        else {
            BowAimbotMod.MC.player.rotationPitch = neededPitch;
        }
    }
    
    private Entity filterEntities(final Stream<Entity> s) {
        Stream<Entity> stream = s.filter(e -> e != null && !e.isDead).filter(e -> (e instanceof EntityLivingBase && ((EntityLivingBase)e).getHealth() > 0.0f) || e instanceof EntityEnderCrystal).filter(e -> e != WMinecraft.getPlayer()).filter(e -> !(e instanceof EntityFakePlayer)).filter(e -> EntityUtils.isCorrectTeam(e)).filter(e -> !BowAimbotMod.WURST.getFriends().contains(e.getName()));
        if (this.filterPlayers.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer));
        }
        if (this.filterSleeping.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPlayer) || !((EntityPlayer)e).isPlayerSleeping());
        }
        if (this.filterFlying.getValue() > 0.0) {
            stream = stream.filter(e -> {
                if (!(e instanceof EntityPlayer)) {
                    return true;
                }
                else {
                    final Box box = e.getEntityBoundingBox();
                    final Box box2 = box.union(box.offset(0.0, -this.filterFlying.getValue(), 0.0));
                    return WWorld.collidesWithAnyBlock(box2);
                }
            });
        }
        if (this.filterMonsters.isChecked()) {
            stream = stream.filter(e -> !(e instanceof IMob));
        }
        if (this.filterPigmen.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityPigZombie));
        }
        if (this.filterEndermen.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityEnderman));
        }
        if (this.filterAnimals.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityAgeable) && !(e instanceof EntityAmbientCreature) && !(e instanceof EntityWaterMob));
        }
        if (this.filterBabies.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityAgeable) || !((EntityAgeable)e).isChild());
        }
        if (this.filterPets.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityTameable) || !((EntityTameable)e).isTamed()).filter(e -> !(e instanceof AbstractHorse) || !((AbstractHorse)e).isTame());
        }
        if (this.filterTraders.isChecked()) {
            stream = stream.filter(e -> !(e instanceof IMerchant));
        }
        if (this.filterGolems.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityGolem));
        }
        if (this.filterInvisible.isChecked()) {
            stream = stream.filter(e -> !e.isInvisible());
        }
        if (this.filterNamed.isChecked()) {
            stream = stream.filter(e -> !e.hasCustomName());
        }
        if (this.filterStands.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityArmorStand));
        }
        if (this.filterCrystals.isChecked()) {
            stream = stream.filter(e -> !(e instanceof EntityEnderCrystal));
        }
        return stream.min(this.priority.getSelected().comparator).orElse(null);
    }
    
    @Override
    public void onRender(final float partialTicks) {
        if (this.target == null) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(-BowAimbotMod.MC.getRenderManager().renderPosX, -BowAimbotMod.MC.getRenderManager().renderPosY, -BowAimbotMod.MC.getRenderManager().renderPosZ);
        GL11.glTranslated(this.target.posX, this.target.posY, this.target.posZ);
        final double boxWidth = this.target.width + 0.1;
        final double boxHeight = this.target.height + 0.1;
        GL11.glScaled(boxWidth, boxHeight, boxWidth);
        GL11.glTranslated(0.0, 0.5, 0.0);
        final double v = 1.0f / this.velocity;
        GL11.glScaled(v, v, v);
        GL11.glColor4d(1.0, 0.0, 0.0, (double)(0.5f * this.velocity));
        RenderUtils.drawOutlinedBox(BowAimbotMod.TARGET_BOX);
        GL11.glColor4d(1.0, 0.0, 0.0, (double)(0.25f * this.velocity));
        RenderUtils.drawSolidBox(BowAimbotMod.TARGET_BOX);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
    
    @Override
    public void onRenderGUI() {
        if (this.target == null) {
            return;
        }
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GL11.glPushMatrix();
        String message;
        if (this.velocity < 1.0f) {
            message = "Charging: " + (int)(this.velocity * 100.0f) + "%";
        }
        else {
            message = "Target Locked";
        }
        final ScaledResolution sr = new ScaledResolution(BowAimbotMod.MC);
        final int msgWidth = Fonts.segoe15.getStringWidth(message);
        GL11.glTranslated((double)(sr.getScaledWidth() / 2 - msgWidth / 2), (double)(sr.getScaledHeight() / 2 + 1), 0.0);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        GL11.glBegin(7);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d((double)(msgWidth + 3), 0.0);
        GL11.glVertex2d((double)(msgWidth + 3), 10.0);
        GL11.glVertex2d(0.0, 10.0);
        GL11.glEnd();
        GL11.glEnable(3553);
        Fonts.segoe15.drawString(message, 2, -1, -1);
        GL11.glPopMatrix();
        GL11.glEnable(2884);
        GL11.glDisable(3042);
    }
    
    private enum Priority
    {
        DISTANCE("Distance", e -> BowAimbotMod.MC.player.getDistanceSqToEntity(e)), 
        ANGLE("Angle", e -> RotationUtils.getAngleToServerRotation(e.getEntityBoundingBox().getCenter())), 
        HEALTH("Health", e -> (e instanceof EntityLivingBase) ? ((EntityLivingBase)e).getHealth() : Double.POSITIVE_INFINITY);
        
        private final String name;
        private final Comparator<Entity> comparator;
        
        private Priority(final String name, final ToDoubleFunction<Entity> keyExtractor) {
            this.name = name;
            this.comparator = Comparator.comparingDouble((ToDoubleFunction<? super Entity>)keyExtractor);
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }
}
