// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.render;

import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.Hack;

@SearchTags({ "camera noclip", "camera no clip" })
@Bypasses
public final class CameraNoClipMod extends Hack
{
    public CameraNoClipMod() {
        super("CameraNoClip", "Allows the camera in 3rd person to go through walls.");
        this.setCategory(Category.RENDER);
    }
}
