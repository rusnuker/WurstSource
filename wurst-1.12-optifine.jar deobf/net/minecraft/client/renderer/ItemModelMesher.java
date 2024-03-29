// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.item.Item;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import java.util.Map;

public class ItemModelMesher
{
    private final Map<Integer, ModelResourceLocation> simpleShapes;
    private final Map<Integer, IBakedModel> simpleShapesCache;
    private final Map<Item, ItemMeshDefinition> shapers;
    private final ModelManager modelManager;
    
    public ItemModelMesher(final ModelManager modelManager) {
        this.simpleShapes = Maps.newHashMap();
        this.simpleShapesCache = Maps.newHashMap();
        this.shapers = Maps.newHashMap();
        this.modelManager = modelManager;
    }
    
    public TextureAtlasSprite getParticleIcon(final Item item) {
        return this.getParticleIcon(item, 0);
    }
    
    public TextureAtlasSprite getParticleIcon(final Item item, final int meta) {
        return this.getItemModel(new ItemStack(item, 1, meta)).getParticleTexture();
    }
    
    public IBakedModel getItemModel(final ItemStack stack) {
        final Item item = stack.getItem();
        IBakedModel ibakedmodel = this.getItemModel(item, this.getMetadata(stack));
        if (ibakedmodel == null) {
            final ItemMeshDefinition itemmeshdefinition = this.shapers.get(item);
            if (itemmeshdefinition != null) {
                ibakedmodel = this.modelManager.getModel(itemmeshdefinition.getModelLocation(stack));
            }
        }
        if (ibakedmodel == null) {
            ibakedmodel = this.modelManager.getMissingModel();
        }
        return ibakedmodel;
    }
    
    protected int getMetadata(final ItemStack stack) {
        return (stack.getMaxDamage() > 0) ? 0 : stack.getMetadata();
    }
    
    @Nullable
    protected IBakedModel getItemModel(final Item item, final int meta) {
        return this.simpleShapesCache.get(this.getIndex(item, meta));
    }
    
    private int getIndex(final Item item, final int meta) {
        return Item.getIdFromItem(item) << 16 | meta;
    }
    
    public void register(final Item item, final int meta, final ModelResourceLocation location) {
        this.simpleShapes.put(this.getIndex(item, meta), location);
        this.simpleShapesCache.put(this.getIndex(item, meta), this.modelManager.getModel(location));
    }
    
    public void register(final Item item, final ItemMeshDefinition definition) {
        this.shapers.put(item, definition);
    }
    
    public ModelManager getModelManager() {
        return this.modelManager;
    }
    
    public void rebuildCache() {
        this.simpleShapesCache.clear();
        for (final Map.Entry<Integer, ModelResourceLocation> entry : this.simpleShapes.entrySet()) {
            this.simpleShapesCache.put(entry.getKey(), this.modelManager.getModel(entry.getValue()));
        }
    }
}
