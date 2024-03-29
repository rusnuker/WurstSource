// 
// Decompiled by Procyon v0.6.0
// 

package shadersmod.client;

import java.io.IOException;
import java.util.zip.ZipEntry;
import net.minecraft.src.StrUtils;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.io.File;

public class ShaderPackZip implements IShaderPack
{
    protected File packFile;
    protected ZipFile packZipFile;
    
    public ShaderPackZip(final String name, final File file) {
        this.packFile = file;
        this.packZipFile = null;
    }
    
    @Override
    public void close() {
        if (this.packZipFile != null) {
            try {
                this.packZipFile.close();
            }
            catch (final Exception ex) {}
            this.packZipFile = null;
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String s = StrUtils.removePrefix(resName, "/");
            final ZipEntry zipentry = this.packZipFile.getEntry(s);
            return (zipentry == null) ? null : this.packZipFile.getInputStream(zipentry);
        }
        catch (final Exception var4) {
            return null;
        }
    }
    
    @Override
    public boolean hasDirectory(final String resName) {
        try {
            if (this.packZipFile == null) {
                this.packZipFile = new ZipFile(this.packFile);
            }
            final String s = StrUtils.removePrefix(resName, "/");
            final ZipEntry zipentry = this.packZipFile.getEntry(s);
            return zipentry != null;
        }
        catch (final IOException var4) {
            return false;
        }
    }
    
    @Override
    public String getName() {
        return this.packFile.getName();
    }
}
