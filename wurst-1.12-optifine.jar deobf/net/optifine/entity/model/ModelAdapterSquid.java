// 
// Decompiled by Procyon v0.6.0
// 

package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.passive.EntitySquid;

public class ModelAdapterSquid extends ModelAdapter
{
    public ModelAdapterSquid() {
        super(EntitySquid.class, "squid", 0.7f);
    }
    
    @Override
    public ModelBase makeModel() {
        return new ModelSquid();
    }
    
    @Override
    public ModelRenderer getModelRenderer(final ModelBase model, final String modelPart) {
        if (!(model instanceof ModelSquid)) {
            return null;
        }
        final ModelSquid modelsquid = (ModelSquid)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(modelsquid, Reflector.ModelSquid_body);
        }
        final String s = "tentacle";
        if (!modelPart.startsWith(s)) {
            return null;
        }
        final ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(modelsquid, Reflector.ModelSquid_tentacles);
        if (amodelrenderer == null) {
            return null;
        }
        final String s2 = modelPart.substring(s.length());
        int i = Config.parseInt(s2, -1);
        return (--i >= 0 && i < amodelrenderer.length) ? amodelrenderer[i] : null;
    }
    
    @Override
    public IEntityRenderer makeEntityRender(final ModelBase modelBase, final float shadowSize) {
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        final RenderSquid rendersquid = new RenderSquid(rendermanager);
        rendersquid.mainModel = modelBase;
        rendersquid.shadowSize = shadowSize;
        return rendersquid;
    }
}
