// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import net.wurstclient.keybinds.PossibleKeybind;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonArray;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.ItemListEditButton;
import net.wurstclient.clickgui.Component;
import java.util.Collection;
import net.wurstclient.files.ConfigFiles;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import java.util.function.Predicate;
import java.util.Objects;
import net.minecraft.item.Item;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.ArrayList;

public final class ItemListSetting extends Setting
{
    private final ArrayList<String> itemNames;
    private final String[] defaultNames;
    
    public ItemListSetting(final String name, final String description, final String... items) {
        super(name, description);
        this.itemNames = new ArrayList<String>();
        Arrays.stream(items).parallel().map(s -> Item.getByNameOrId(s)).filter(Objects::nonNull).map(b -> Item.REGISTRY.getNameForObject(b).toString()).distinct().sorted().forEachOrdered(s -> this.itemNames.add(s));
        this.defaultNames = this.itemNames.toArray(new String[0]);
    }
    
    public List<String> getItemNames() {
        return Collections.unmodifiableList((List<? extends String>)this.itemNames);
    }
    
    public void add(final Item item) {
        final String name = Item.REGISTRY.getNameForObject(item).toString();
        if (Collections.binarySearch(this.itemNames, name) >= 0) {
            return;
        }
        this.itemNames.add(name);
        Collections.sort(this.itemNames);
        ConfigFiles.SETTINGS.save();
    }
    
    public void remove(final int index) {
        if (index < 0 || index >= this.itemNames.size()) {
            return;
        }
        this.itemNames.remove(index);
        ConfigFiles.SETTINGS.save();
    }
    
    public void resetToDefaults() {
        this.itemNames.clear();
        this.itemNames.addAll(Arrays.asList(this.defaultNames));
        ConfigFiles.SETTINGS.save();
    }
    
    @Override
    public Component getComponent() {
        return new ItemListEditButton(this);
    }
    
    @Override
    public void fromJson(final JsonElement json) {
        if (!json.isJsonArray()) {
            return;
        }
        this.itemNames.clear();
        StreamSupport.stream((Spliterator<Object>)json.getAsJsonArray().spliterator(), true).filter(e -> e.isJsonPrimitive()).filter(e -> e.getAsJsonPrimitive().isString()).map(e -> Item.getByNameOrId(e.getAsString())).filter(Objects::nonNull).map(b -> Item.REGISTRY.getNameForObject(b).toString()).distinct().sorted().forEachOrdered(s -> this.itemNames.add(s));
    }
    
    @Override
    public JsonElement toJson() {
        final JsonArray json = new JsonArray();
        this.itemNames.forEach(s -> jsonArray.add((JsonElement)new JsonPrimitive(s)));
        return (JsonElement)json;
    }
    
    @Override
    public ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        return new ArrayList<PossibleKeybind>();
    }
}
