// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.altmanager;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import net.minecraft.util.Session;
import net.minecraft.client.Minecraft;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public final class LoginManager
{
    public static String login(final String email, final String password) {
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        try {
            auth.logIn();
            Minecraft.getMinecraft().session = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
            return "";
        }
        catch (final AuthenticationUnavailableException e) {
            return "§4§lCannot contact authentication server!";
        }
        catch (final AuthenticationException e2) {
            e2.printStackTrace();
            if (e2.getMessage().contains("Invalid username or password.") || e2.getMessage().toLowerCase().contains("account migrated")) {
                return "§4§lWrong password!";
            }
            return "§4§lCannot contact authentication server!";
        }
        catch (final NullPointerException e3) {
            return "§4§lWrong password!";
        }
    }
    
    public static void changeCrackedName(final String newName) {
        Minecraft.getMinecraft().session = new Session(newName, "", "", "mojang");
    }
}
