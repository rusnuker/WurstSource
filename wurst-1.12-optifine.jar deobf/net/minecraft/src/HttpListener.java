// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

public interface HttpListener
{
    void finished(final HttpRequest p0, final HttpResponse p1);
    
    void failed(final HttpRequest p0, final Exception p1);
}
