// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam;

import java.util.HashMap;

public class VarManager
{
    private final HashMap<String, String> spammerVars;
    private final HashMap<String, String> userVars;
    
    public VarManager() {
        this.spammerVars = new HashMap<String, String>();
        this.userVars = new HashMap<String, String>();
        this.spammerVars.put("lt", "<");
        this.spammerVars.put("gt", ">");
        this.spammerVars.put("sp", " ");
        this.spammerVars.put("br", "\n");
    }
    
    public HashMap<String, String> getSpammerVars() {
        return this.spammerVars;
    }
    
    public void clearUserVars() {
        this.userVars.clear();
    }
    
    public void addUserVar(final String name, final String value) {
        this.userVars.put(name, value);
    }
    
    public String getValueOfVar(final String varName) {
        if (varName.startsWith("_")) {
            return this.spammerVars.get(varName.substring(1));
        }
        return this.userVars.get(varName);
    }
}
