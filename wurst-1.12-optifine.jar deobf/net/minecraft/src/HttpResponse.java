// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.src;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse
{
    private int status;
    private String statusLine;
    private Map<String, String> headers;
    private byte[] body;
    
    public HttpResponse(final int p_i58_1_, final String p_i58_2_, final Map p_i58_3_, final byte[] p_i58_4_) {
        this.status = 0;
        this.statusLine = null;
        this.headers = new LinkedHashMap<String, String>();
        this.body = null;
        this.status = p_i58_1_;
        this.statusLine = p_i58_2_;
        this.headers = p_i58_3_;
        this.body = p_i58_4_;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public String getStatusLine() {
        return this.statusLine;
    }
    
    public Map getHeaders() {
        return this.headers;
    }
    
    public String getHeader(final String p_getHeader_1_) {
        return this.headers.get(p_getHeader_1_);
    }
    
    public byte[] getBody() {
        return this.body;
    }
}
