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
import net.wurstclient.clickgui.components.BlockListEditButton;
import net.wurstclient.clickgui.Component;
import java.util.Collection;
import net.wurstclient.files.ConfigFiles;
import net.minecraft.block.Block;
import java.util.Collections;
import java.util.List;
import net.wurstclient.compatibility.WBlock;
import java.util.function.Predicate;
import java.util.Objects;
import net.wurstclient.util.BlockUtils;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.ArrayList;

public final class BlockListSetting extends Setting
{
    private final ArrayList<String> blockNames;
    private final String[] defaultNames;
    
    public BlockListSetting(final String name, final String description, final String... blocks) {
        super(name, description);
        this.blockNames = new ArrayList<String>();
        Arrays.stream(blocks).parallel().map(s -> BlockUtils.getBlockFromName(s)).filter(Objects::nonNull).map(b -> WBlock.getName(b)).distinct().sorted().forEachOrdered(s -> this.blockNames.add(s));
        this.defaultNames = this.blockNames.toArray(new String[0]);
    }
    
    public List<String> getBlockNames() {
        return Collections.unmodifiableList((List<? extends String>)this.blockNames);
    }
    
    public void add(final Block block) {
        final String name = WBlock.getName(block);
        if (Collections.binarySearch(this.blockNames, name) >= 0) {
            return;
        }
        this.blockNames.add(name);
        Collections.sort(this.blockNames);
        ConfigFiles.SETTINGS.save();
    }
    
    public void remove(final int index) {
        if (index < 0 || index >= this.blockNames.size()) {
            return;
        }
        this.blockNames.remove(index);
        ConfigFiles.SETTINGS.save();
    }
    
    public void resetToDefaults() {
        this.blockNames.clear();
        this.blockNames.addAll(Arrays.asList(this.defaultNames));
        ConfigFiles.SETTINGS.save();
    }
    
    @Override
    public Component getComponent() {
        return new BlockListEditButton(this);
    }
    
    @Override
    public void fromJson(final JsonElement json) {
        if (!json.isJsonArray()) {
            return;
        }
        this.blockNames.clear();
        StreamSupport.stream((Spliterator<Object>)json.getAsJsonArray().spliterator(), true).filter(e -> e.isJsonPrimitive()).filter(e -> e.getAsJsonPrimitive().isString()).map(e -> Block.getBlockFromName(e.getAsString())).filter(Objects::nonNull).map(b -> WBlock.getName(b)).distinct().sorted().forEachOrdered(s -> this.blockNames.add(s));
    }
    
    @Override
    public JsonElement toJson() {
        final JsonArray json = new JsonArray();
        this.blockNames.forEach(s -> jsonArray.add((JsonElement)new JsonPrimitive(s)));
        return (JsonElement)json;
    }
    
    @Override
    public ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        return new ArrayList<PossibleKeybind>();
    }
}
