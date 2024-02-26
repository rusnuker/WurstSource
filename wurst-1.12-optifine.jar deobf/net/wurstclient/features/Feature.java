// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import net.wurstclient.keybinds.PossibleKeybind;
import java.lang.annotation.Annotation;
import net.wurstclient.settings.Setting;
import java.util.ArrayList;
import net.wurstclient.Category;
import net.minecraft.client.Minecraft;
import net.wurstclient.event.EventManager;
import net.wurstclient.WurstClient;

public abstract class Feature
{
    protected static final WurstClient WURST;
    protected static final EventManager EVENTS;
    protected static final Minecraft MC;
    private Category category;
    private final ArrayList<Setting> settings;
    private final String helpPage;
    private final String searchTags;
    
    static {
        WURST = WurstClient.INSTANCE;
        EVENTS = Feature.WURST.events;
        MC = Minecraft.getMinecraft();
    }
    
    public Feature() {
        this.settings = new ArrayList<Setting>();
        this.helpPage = (this.getClass().isAnnotationPresent(HelpPage.class) ? this.getClass().getAnnotation(HelpPage.class).value() : "");
        this.searchTags = (this.getClass().isAnnotationPresent(SearchTags.class) ? String.join("§", (CharSequence[])this.getClass().getAnnotation(SearchTags.class).value()) : "");
    }
    
    public abstract String getName();
    
    public abstract String getType();
    
    public final Category getCategory() {
        return this.category;
    }
    
    protected final void setCategory(final Category category) {
        this.category = category;
    }
    
    public abstract String getDescription();
    
    public abstract boolean isEnabled();
    
    public abstract boolean isBlocked();
    
    public final ArrayList<Setting> getSettings() {
        return this.settings;
    }
    
    protected final void addSetting(final Setting setting) {
        this.settings.add(setting);
    }
    
    public abstract ArrayList<PossibleKeybind> getPossibleKeybinds();
    
    public abstract String getPrimaryAction();
    
    public abstract void doPrimaryAction();
    
    public final String getHelpPage() {
        return this.helpPage;
    }
    
    public final String getSearchTags() {
        return this.searchTags;
    }
    
    public abstract Feature[] getSeeAlso();
}
