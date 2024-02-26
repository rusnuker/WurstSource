// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui.recipebook;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.World;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketRecipePlacement;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import java.util.Iterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Collection;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Locale;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.client.util.RecipeBookClient;
import javax.annotation.Nullable;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.stats.RecipeBook;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.client.gui.GuiButtonToggle;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.Gui;

public class GuiRecipeBook extends Gui implements IRecipeUpdateListener
{
    protected static final ResourceLocation field_191894_a;
    private int field_191903_n;
    private int field_191904_o;
    private int field_191905_p;
    private static final Logger field_193959_i;
    private final GhostRecipe field_191915_z;
    private final List<GuiButtonRecipeTab> field_193018_j;
    private GuiButtonRecipeTab field_191913_x;
    private GuiButtonToggle field_193960_m;
    private Container field_191908_s;
    private InventoryCrafting field_193961_o;
    private Minecraft field_191888_F;
    private GuiTextField field_193962_q;
    private String field_193963_r;
    private RecipeBook field_193964_s;
    private final RecipeBookPage field_193022_s;
    private RecipeItemHelper field_193965_u;
    private int field_193966_v;
    
    static {
        field_191894_a = new ResourceLocation("textures/gui/recipe_book.png");
        field_193959_i = LogManager.getLogger();
    }
    
    public GuiRecipeBook() {
        this.field_191915_z = new GhostRecipe();
        this.field_193018_j = Lists.newArrayList((Object[])new GuiButtonRecipeTab[] { new GuiButtonRecipeTab(0, CreativeTabs.SEARCH), new GuiButtonRecipeTab(0, CreativeTabs.TOOLS), new GuiButtonRecipeTab(0, CreativeTabs.BUILDING_BLOCKS), new GuiButtonRecipeTab(0, CreativeTabs.MISC), new GuiButtonRecipeTab(0, CreativeTabs.REDSTONE) });
        this.field_193963_r = "";
        this.field_193022_s = new RecipeBookPage();
        this.field_193965_u = new RecipeItemHelper();
    }
    
    public void func_191856_a(final int p_191856_1_, final int p_191856_2_, final Minecraft p_191856_3_, final boolean p_191856_4_, final Container p_191856_5_, final InventoryCrafting p_191856_6_) {
        this.field_191888_F = p_191856_3_;
        this.field_191904_o = p_191856_1_;
        this.field_191905_p = p_191856_2_;
        this.field_191908_s = p_191856_5_;
        this.field_193961_o = p_191856_6_;
        this.field_193964_s = p_191856_3_.player.func_192035_E();
        this.field_193966_v = p_191856_3_.player.inventory.func_194015_p();
        (this.field_191913_x = this.field_193018_j.get(0)).func_191753_b(true);
        if (this.func_191878_b()) {
            this.func_193014_a(p_191856_4_, p_191856_6_);
        }
        Keyboard.enableRepeatEvents(true);
    }
    
    public void func_193014_a(final boolean p_193014_1_, final InventoryCrafting p_193014_2_) {
        this.field_191903_n = (p_193014_1_ ? 0 : 86);
        final int i = (this.field_191904_o - 147) / 2 - this.field_191903_n;
        final int j = (this.field_191905_p - 166) / 2;
        this.field_193965_u.func_194119_a();
        this.field_191888_F.player.inventory.func_194016_a(this.field_193965_u, false);
        p_193014_2_.func_194018_a(this.field_193965_u);
        (this.field_193962_q = new GuiTextField(0, this.field_191888_F.fontRendererObj, i + 25, j + 14, 80, this.field_191888_F.fontRendererObj.FONT_HEIGHT + 5)).setMaxStringLength(50);
        this.field_193962_q.setEnableBackgroundDrawing(false);
        this.field_193962_q.setVisible(true);
        this.field_193962_q.setTextColor(16777215);
        this.field_193022_s.func_194194_a(this.field_191888_F, i, j);
        this.field_193022_s.func_193732_a(this);
        (this.field_193960_m = new GuiButtonToggle(0, i + 110, j + 12, 26, 16, this.field_193964_s.func_192815_c())).func_191751_a(152, 41, 28, 18, GuiRecipeBook.field_191894_a);
        this.func_193003_g(false);
        this.func_193949_f();
    }
    
    public void func_191871_c() {
        Keyboard.enableRepeatEvents(false);
    }
    
    public int func_193011_a(final boolean p_193011_1_, final int p_193011_2_, final int p_193011_3_) {
        int i;
        if (this.func_191878_b() && !p_193011_1_) {
            i = 177 + (p_193011_2_ - p_193011_3_ - 200) / 2;
        }
        else {
            i = (p_193011_2_ - p_193011_3_) / 2;
        }
        return i;
    }
    
    public void func_191866_a() {
        this.func_193006_a(!this.func_191878_b());
    }
    
    public boolean func_191878_b() {
        return this.field_193964_s.func_192812_b();
    }
    
    private void func_193006_a(final boolean p_193006_1_) {
        this.field_193964_s.func_192813_a(p_193006_1_);
        if (!p_193006_1_) {
            this.field_193022_s.func_194200_c();
        }
        this.func_193956_j();
    }
    
    public void func_191874_a(@Nullable final Slot p_191874_1_) {
        if (p_191874_1_ != null && p_191874_1_.slotNumber <= 9) {
            this.field_191915_z.func_192682_a();
            if (this.func_191878_b()) {
                this.func_193942_g();
            }
        }
    }
    
    private void func_193003_g(final boolean p_193003_1_) {
        final List<RecipeList> list = RecipeBookClient.field_194086_e.get(this.field_191913_x.func_191764_e());
        list.forEach(p_193944_1_ -> p_193944_1_.func_194210_a(this.field_193965_u, this.field_193961_o.getWidth(), this.field_193961_o.getHeight(), this.field_193964_s));
        final List<RecipeList> list2 = Lists.newArrayList((Iterable)list);
        list2.removeIf(p_193952_0_ -> !p_193952_0_.func_194209_a());
        list2.removeIf(p_193953_0_ -> !p_193953_0_.func_194212_c());
        final String s = this.field_193962_q.getText();
        if (!s.isEmpty()) {
            final ObjectSet<RecipeList> objectset = (ObjectSet<RecipeList>)new ObjectLinkedOpenHashSet((Collection)this.field_191888_F.func_193987_a(SearchTreeManager.field_194012_b).func_194038_a(s.toLowerCase(Locale.ROOT)));
            list2.removeIf(p_193947_1_ -> !set.contains((Object)p_193947_1_));
        }
        if (this.field_193964_s.func_192815_c()) {
            list2.removeIf(p_193958_0_ -> !p_193958_0_.func_192708_c());
        }
        this.field_193022_s.func_194192_a(list2, p_193003_1_);
    }
    
    private void func_193949_f() {
        final int i = (this.field_191904_o - 147) / 2 - this.field_191903_n - 30;
        final int j = (this.field_191905_p - 166) / 2 + 3;
        final int k = 27;
        int l = 0;
        for (final GuiButtonRecipeTab guibuttonrecipetab : this.field_193018_j) {
            final CreativeTabs creativetabs = guibuttonrecipetab.func_191764_e();
            if (creativetabs == CreativeTabs.SEARCH) {
                guibuttonrecipetab.visible = true;
                guibuttonrecipetab.func_191752_c(i, j + 27 * l++);
            }
            else {
                if (!guibuttonrecipetab.func_193919_e()) {
                    continue;
                }
                guibuttonrecipetab.func_191752_c(i, j + 27 * l++);
                guibuttonrecipetab.func_193918_a(this.field_191888_F);
            }
        }
    }
    
    public void func_193957_d() {
        if (this.func_191878_b() && this.field_193966_v != this.field_191888_F.player.inventory.func_194015_p()) {
            this.func_193942_g();
            this.field_193966_v = this.field_191888_F.player.inventory.func_194015_p();
        }
    }
    
    private void func_193942_g() {
        this.field_193965_u.func_194119_a();
        this.field_191888_F.player.inventory.func_194016_a(this.field_193965_u, false);
        this.field_193961_o.func_194018_a(this.field_193965_u);
        this.func_193003_g(false);
    }
    
    public void func_191861_a(final int p_191861_1_, final int p_191861_2_, final float p_191861_3_) {
        if (this.func_191878_b()) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0f, 0.0f, 100.0f);
            this.field_191888_F.getTextureManager().bindTexture(GuiRecipeBook.field_191894_a);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            final int i = (this.field_191904_o - 147) / 2 - this.field_191903_n;
            final int j = (this.field_191905_p - 166) / 2;
            this.drawTexturedModalRect(i, j, 1, 1, 147, 166);
            this.field_193962_q.drawTextBox();
            RenderHelper.disableStandardItemLighting();
            for (final GuiButtonRecipeTab guibuttonrecipetab : this.field_193018_j) {
                guibuttonrecipetab.drawButton(this.field_191888_F, p_191861_1_, p_191861_2_, p_191861_3_);
            }
            this.field_193960_m.drawButton(this.field_191888_F, p_191861_1_, p_191861_2_, p_191861_3_);
            this.field_193022_s.func_194191_a(i, j, p_191861_1_, p_191861_2_, p_191861_3_);
            GlStateManager.popMatrix();
        }
    }
    
    public void func_191876_c(final int p_191876_1_, final int p_191876_2_, final int p_191876_3_, final int p_191876_4_) {
        if (this.func_191878_b()) {
            this.field_193022_s.func_193721_a(p_191876_3_, p_191876_4_);
            if (this.field_193960_m.isMouseOver()) {
                final String s1 = I18n.format(this.field_193960_m.func_191754_c() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all", new Object[0]);
                if (this.field_191888_F.currentScreen != null) {
                    this.field_191888_F.currentScreen.drawCreativeTabHoveringText(s1, p_191876_3_, p_191876_4_);
                }
            }
            this.func_193015_d(p_191876_1_, p_191876_2_, p_191876_3_, p_191876_4_);
        }
    }
    
    private void func_193015_d(final int p_193015_1_, final int p_193015_2_, final int p_193015_3_, final int p_193015_4_) {
        ItemStack itemstack = null;
        for (int i = 0; i < this.field_191915_z.func_192684_b(); ++i) {
            final GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.field_191915_z.func_192681_a(i);
            final int j = ghostrecipe$ghostingredient.func_193713_b() + p_193015_1_;
            final int k = ghostrecipe$ghostingredient.func_193712_c() + p_193015_2_;
            if (p_193015_3_ >= j && p_193015_4_ >= k && p_193015_3_ < j + 16 && p_193015_4_ < k + 16) {
                itemstack = ghostrecipe$ghostingredient.func_194184_c();
            }
        }
        if (itemstack != null && this.field_191888_F.currentScreen != null) {
            this.field_191888_F.currentScreen.drawHoveringText(this.field_191888_F.currentScreen.func_191927_a(itemstack), p_193015_3_, p_193015_4_);
        }
    }
    
    public void func_191864_a(final int p_191864_1_, final int p_191864_2_, final boolean p_191864_3_, final float p_191864_4_) {
        this.field_191915_z.func_194188_a(this.field_191888_F, p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
    }
    
    public boolean func_191862_a(final int p_191862_1_, final int p_191862_2_, final int p_191862_3_) {
        if (!this.func_191878_b() || this.field_191888_F.player.isSpectator()) {
            return false;
        }
        if (this.field_193022_s.func_194196_a(p_191862_1_, p_191862_2_, p_191862_3_, (this.field_191904_o - 147) / 2 - this.field_191903_n, (this.field_191905_p - 166) / 2, 147, 166)) {
            final IRecipe irecipe = this.field_193022_s.func_194193_a();
            final RecipeList recipelist = this.field_193022_s.func_194199_b();
            if (irecipe != null && recipelist != null) {
                this.func_193945_a(irecipe, recipelist);
                if (!this.func_191880_f() && p_191862_3_ == 0) {
                    this.func_193006_a(false);
                }
            }
            return true;
        }
        if (p_191862_3_ != 0) {
            return false;
        }
        if (this.field_193962_q.mouseClicked(p_191862_1_, p_191862_2_, p_191862_3_)) {
            return true;
        }
        if (this.field_193960_m.mousePressed(this.field_191888_F, p_191862_1_, p_191862_2_)) {
            final boolean flag = !this.field_193964_s.func_192815_c();
            this.field_193964_s.func_192810_b(flag);
            this.field_193960_m.func_191753_b(flag);
            this.field_193960_m.playPressSound(this.field_191888_F.getSoundHandler());
            this.func_193956_j();
            this.func_193003_g(false);
            return true;
        }
        for (final GuiButtonRecipeTab guibuttonrecipetab : this.field_193018_j) {
            if (guibuttonrecipetab.mousePressed(this.field_191888_F, p_191862_1_, p_191862_2_)) {
                if (this.field_191913_x != guibuttonrecipetab) {
                    guibuttonrecipetab.playPressSound(this.field_191888_F.getSoundHandler());
                    this.field_191913_x.func_191753_b(false);
                    (this.field_191913_x = guibuttonrecipetab).func_191753_b(true);
                    this.func_193003_g(true);
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean func_193955_c(final int p_193955_1_, final int p_193955_2_, final int p_193955_3_, final int p_193955_4_, final int p_193955_5_, final int p_193955_6_) {
        if (!this.func_191878_b()) {
            return true;
        }
        final boolean flag = p_193955_1_ < p_193955_3_ || p_193955_2_ < p_193955_4_ || p_193955_1_ >= p_193955_3_ + p_193955_5_ || p_193955_2_ >= p_193955_4_ + p_193955_6_;
        final boolean flag2 = p_193955_3_ - 147 < p_193955_1_ && p_193955_1_ < p_193955_3_ && p_193955_4_ < p_193955_2_ && p_193955_2_ < p_193955_4_ + p_193955_6_;
        return flag && !flag2 && !this.field_191913_x.mousePressed(this.field_191888_F, p_193955_1_, p_193955_2_);
    }
    
    public boolean func_191859_a(final char p_191859_1_, final int p_191859_2_) {
        if (!this.func_191878_b() || this.field_191888_F.player.isSpectator()) {
            return false;
        }
        if (p_191859_2_ == 1 && !this.func_191880_f()) {
            this.func_193006_a(false);
            return true;
        }
        if (GameSettings.isKeyDown(this.field_191888_F.gameSettings.keyBindChat) && !this.field_193962_q.isFocused()) {
            this.field_193962_q.setFocused(true);
        }
        else if (this.field_193962_q.textboxKeyTyped(p_191859_1_, p_191859_2_)) {
            final String s1 = this.field_193962_q.getText().toLowerCase(Locale.ROOT);
            this.func_193716_a(s1);
            if (!s1.equals(this.field_193963_r)) {
                this.func_193003_g(false);
                this.field_193963_r = s1;
            }
            return true;
        }
        return false;
    }
    
    private void func_193716_a(final String p_193716_1_) {
        if ("excitedze".equals(p_193716_1_)) {
            final LanguageManager languagemanager = this.field_191888_F.getLanguageManager();
            final Language language = languagemanager.func_191960_a("en_pt");
            if (languagemanager.getCurrentLanguage().compareTo(language) == 0) {
                return;
            }
            languagemanager.setCurrentLanguage(language);
            this.field_191888_F.gameSettings.language = language.getLanguageCode();
            this.field_191888_F.refreshResources();
            this.field_191888_F.fontRendererObj.setUnicodeFlag(this.field_191888_F.getLanguageManager().isCurrentLocaleUnicode() || this.field_191888_F.gameSettings.forceUnicodeFont);
            this.field_191888_F.fontRendererObj.setBidiFlag(languagemanager.isCurrentLanguageBidirectional());
            this.field_191888_F.gameSettings.saveOptions();
        }
    }
    
    private boolean func_191880_f() {
        return this.field_191903_n == 86;
    }
    
    public void func_193948_e() {
        this.func_193949_f();
        if (this.func_191878_b()) {
            this.func_193003_g(false);
        }
    }
    
    @Override
    public void func_193001_a(final List<IRecipe> p_193001_1_) {
        for (final IRecipe irecipe : p_193001_1_) {
            this.field_191888_F.player.func_193103_a(irecipe);
        }
    }
    
    private void func_193945_a(final IRecipe p_193945_1_, final RecipeList p_193945_2_) {
        final boolean flag = p_193945_2_.func_194213_a(p_193945_1_);
        InventoryCraftResult inventorycraftresult = null;
        if (this.field_191908_s instanceof ContainerWorkbench) {
            inventorycraftresult = ((ContainerWorkbench)this.field_191908_s).craftResult;
        }
        else if (this.field_191908_s instanceof ContainerPlayer) {
            inventorycraftresult = ((ContainerPlayer)this.field_191908_s).craftResult;
        }
        if (inventorycraftresult != null) {
            if (!flag && this.field_191915_z.func_192686_c() == p_193945_1_) {
                return;
            }
            if (!this.func_193941_i() && !this.field_191888_F.player.isCreative()) {
                return;
            }
            if (flag) {
                this.func_193950_a(p_193945_1_, this.field_191908_s.inventorySlots, this.field_191908_s.windowId, inventorycraftresult);
            }
            else {
                final List<CPacketRecipePlacement.ItemMove> list2 = this.func_193954_a(inventorycraftresult);
                this.func_193951_a(p_193945_1_, this.field_191908_s.inventorySlots);
                if (!list2.isEmpty()) {
                    this.field_191888_F.playerController.func_192831_a(this.field_191908_s.windowId, list2, Lists.newArrayList(), this.field_191888_F.player);
                    if (this.field_193964_s.func_192815_c()) {
                        this.field_191888_F.player.inventory.markDirty();
                    }
                }
            }
            if (!this.func_191880_f()) {
                this.func_191866_a();
            }
        }
    }
    
    private void func_193950_a(final IRecipe p_193950_1_, final List<Slot> p_193950_2_, final int p_193950_3_, final InventoryCraftResult p_193950_4_) {
        final boolean flag = p_193950_1_.matches(this.field_193961_o, this.field_191888_F.world);
        final int i = this.field_193965_u.func_194114_b(p_193950_1_, null);
        if (flag) {
            boolean flag2 = true;
            for (int j = 0; j < this.field_193961_o.getSizeInventory(); ++j) {
                final ItemStack itemstack = this.field_193961_o.getInvStack(j);
                if (!itemstack.func_190926_b() && i > itemstack.func_190916_E()) {
                    flag2 = false;
                }
            }
            if (flag2) {
                return;
            }
        }
        final int i2 = this.func_193943_a(i, flag);
        final IntList intlist = (IntList)new IntArrayList();
        if (this.field_193965_u.func_194118_a(p_193950_1_, intlist, i2)) {
            int j2 = i2;
            for (final int k : intlist) {
                final int l = RecipeItemHelper.func_194115_b(k).getMaxStackSize();
                if (l < j2) {
                    j2 = l;
                }
            }
            if (this.field_193965_u.func_194118_a(p_193950_1_, intlist, j2)) {
                final List<CPacketRecipePlacement.ItemMove> list2 = this.func_193954_a(p_193950_4_);
                final List<CPacketRecipePlacement.ItemMove> list3 = Lists.newArrayList();
                this.func_193013_a(p_193950_1_, p_193950_2_, j2, intlist, list3);
                this.field_191888_F.playerController.func_192831_a(p_193950_3_, list2, list3, this.field_191888_F.player);
                this.field_191888_F.player.inventory.markDirty();
            }
        }
    }
    
    private List<CPacketRecipePlacement.ItemMove> func_193954_a(final InventoryCraftResult p_193954_1_) {
        this.field_191915_z.func_192682_a();
        final InventoryPlayer inventoryplayer = this.field_191888_F.player.inventory;
        final List<CPacketRecipePlacement.ItemMove> list2 = Lists.newArrayList();
        for (int i = 0; i < this.field_193961_o.getSizeInventory(); ++i) {
            final ItemStack itemstack = this.field_193961_o.getInvStack(i);
            if (!itemstack.func_190926_b()) {
                while (itemstack.func_190916_E() > 0) {
                    int j = inventoryplayer.storeItemStack(itemstack);
                    if (j == -1) {
                        j = inventoryplayer.getFirstEmptyStack();
                    }
                    final ItemStack itemstack2 = itemstack.copy();
                    itemstack2.func_190920_e(1);
                    if (inventoryplayer.func_191971_c(j, itemstack2)) {
                        itemstack2.func_190917_f(1);
                    }
                    else {
                        GuiRecipeBook.field_193959_i.error("Can't find any space for item in inventory");
                    }
                    this.field_193961_o.decrStackSize(i, 1);
                    final int k = i + 1;
                    list2.add(new CPacketRecipePlacement.ItemMove(itemstack2.copy(), k, j));
                }
            }
        }
        this.field_193961_o.clear();
        p_193954_1_.clear();
        return list2;
    }
    
    private int func_193943_a(final int p_193943_1_, final boolean p_193943_2_) {
        int i = 1;
        if (GuiScreen.isShiftKeyDown()) {
            i = p_193943_1_;
        }
        else if (p_193943_2_) {
            i = 64;
            for (int j = 0; j < this.field_193961_o.getSizeInventory(); ++j) {
                final ItemStack itemstack = this.field_193961_o.getInvStack(j);
                if (!itemstack.func_190926_b() && i > itemstack.func_190916_E()) {
                    i = itemstack.func_190916_E();
                }
            }
            if (i < 64) {
                ++i;
            }
        }
        return i;
    }
    
    private void func_193013_a(final IRecipe p_193013_1_, final List<Slot> p_193013_2_, final int p_193013_3_, final IntList p_193013_4_, final List<CPacketRecipePlacement.ItemMove> p_193013_5_) {
        int i = this.field_193961_o.getWidth();
        int j = this.field_193961_o.getHeight();
        if (p_193013_1_ instanceof ShapedRecipes) {
            final ShapedRecipes shapedrecipes = (ShapedRecipes)p_193013_1_;
            i = shapedrecipes.func_192403_f();
            j = shapedrecipes.func_192404_g();
        }
        int j2 = 1;
        final Iterator<Integer> iterator = (Iterator<Integer>)p_193013_4_.iterator();
        for (int k = 0; k < this.field_193961_o.getWidth() && j != k; ++k) {
            for (int l = 0; l < this.field_193961_o.getHeight(); ++l) {
                if (i == l || !iterator.hasNext()) {
                    j2 += this.field_193961_o.getWidth() - l;
                    break;
                }
                final Slot slot = p_193013_2_.get(j2);
                final ItemStack itemstack = RecipeItemHelper.func_194115_b(iterator.next());
                if (itemstack.func_190926_b()) {
                    ++j2;
                }
                else {
                    for (int i2 = 0; i2 < p_193013_3_; ++i2) {
                        final CPacketRecipePlacement.ItemMove cpacketrecipeplacement$itemmove = this.func_193946_a(j2, slot, itemstack);
                        if (cpacketrecipeplacement$itemmove != null) {
                            p_193013_5_.add(cpacketrecipeplacement$itemmove);
                        }
                    }
                    ++j2;
                }
            }
            if (!iterator.hasNext()) {
                break;
            }
        }
    }
    
    @Nullable
    private CPacketRecipePlacement.ItemMove func_193946_a(final int p_193946_1_, final Slot p_193946_2_, final ItemStack p_193946_3_) {
        final InventoryPlayer inventoryplayer = this.field_191888_F.player.inventory;
        final int i = inventoryplayer.func_194014_c(p_193946_3_);
        if (i == -1) {
            return null;
        }
        final ItemStack itemstack = inventoryplayer.getInvStack(i).copy();
        if (itemstack.func_190926_b()) {
            GuiRecipeBook.field_193959_i.error("Matched: " + p_193946_3_.getUnlocalizedName() + " with empty item.");
            return null;
        }
        if (itemstack.func_190916_E() > 1) {
            inventoryplayer.decrStackSize(i, 1);
        }
        else {
            inventoryplayer.removeStackFromSlot(i);
        }
        itemstack.func_190920_e(1);
        if (p_193946_2_.getStack().func_190926_b()) {
            p_193946_2_.putStack(itemstack);
        }
        else {
            p_193946_2_.getStack().func_190917_f(1);
        }
        return new CPacketRecipePlacement.ItemMove(itemstack, p_193946_1_, i);
    }
    
    private boolean func_193941_i() {
        final InventoryPlayer inventoryplayer = this.field_191888_F.player.inventory;
        for (int i = 0; i < this.field_193961_o.getSizeInventory(); ++i) {
            final ItemStack itemstack = this.field_193961_o.getInvStack(i);
            if (!itemstack.func_190926_b()) {
                int j = inventoryplayer.storeItemStack(itemstack);
                if (j == -1) {
                    j = inventoryplayer.getFirstEmptyStack();
                }
                if (j == -1) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void func_193951_a(final IRecipe p_193951_1_, final List<Slot> p_193951_2_) {
        final ItemStack itemstack = p_193951_1_.getRecipeOutput();
        this.field_191915_z.func_192685_a(p_193951_1_);
        this.field_191915_z.func_194187_a(Ingredient.func_193369_a(itemstack), p_193951_2_.get(0).xDisplayPosition, p_193951_2_.get(0).yDisplayPosition);
        final int i = this.field_193961_o.getWidth();
        final int j = this.field_193961_o.getHeight();
        final int k = (p_193951_1_ instanceof ShapedRecipes) ? ((ShapedRecipes)p_193951_1_).func_192403_f() : i;
        int l = 1;
        final Iterator<Ingredient> iterator = p_193951_1_.func_192400_c().iterator();
        for (int i2 = 0; i2 < j; ++i2) {
            for (int j2 = 0; j2 < k; ++j2) {
                if (!iterator.hasNext()) {
                    return;
                }
                final Ingredient ingredient = iterator.next();
                if (ingredient != Ingredient.field_193370_a) {
                    final Slot slot = p_193951_2_.get(l);
                    this.field_191915_z.func_194187_a(ingredient, slot.xDisplayPosition, slot.yDisplayPosition);
                }
                ++l;
            }
            if (k < i) {
                l += i - k;
            }
        }
    }
    
    private void func_193956_j() {
        if (this.field_191888_F.getConnection() != null) {
            this.field_191888_F.getConnection().sendPacket(new CPacketRecipeInfo(this.func_191878_b(), this.field_193964_s.func_192815_c()));
        }
    }
}
