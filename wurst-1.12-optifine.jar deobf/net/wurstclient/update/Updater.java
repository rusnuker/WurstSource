// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.update;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;
import java.net.URI;
import com.google.gson.JsonObject;
import java.util.Iterator;
import com.google.gson.JsonArray;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.TextComponentString;
import com.google.gson.JsonElement;
import net.wurstclient.WurstClient;
import net.wurstclient.util.ChatUtils;
import net.minecraft.util.text.ITextComponent;
import net.wurstclient.events.UpdateListener;

public final class Updater implements UpdateListener
{
    private boolean outdated;
    private String latestVersionString;
    private ITextComponent component;
    
    @Override
    public void onUpdate() {
        if (this.component != null) {
            ChatUtils.component(this.component);
        }
        WurstClient.INSTANCE.events.remove(UpdateListener.class, this);
    }
    
    public void checkForUpdate() {
        final Version currentVersion = new Version("6.35.3");
        Version latestVersion = null;
        try {
            final JsonArray json = this.fetchJson("https://api.github.com/repos/Wurst-Imperium/Wurst-MCX2/releases").getAsJsonArray();
            for (final JsonElement element : json) {
                final JsonObject release = element.getAsJsonObject();
                if (!currentVersion.isPreRelease() && release.get("prerelease").getAsBoolean()) {
                    continue;
                }
                if (!this.containsCompatibleAsset(release.get("assets").getAsJsonArray())) {
                    continue;
                }
                this.latestVersionString = release.get("tag_name").getAsString().substring(1);
                latestVersion = new Version(this.latestVersionString);
                break;
            }
            if (latestVersion == null) {
                throw new NullPointerException("Latest version is missing!");
            }
            System.out.println("[Updater] Current version: " + currentVersion);
            System.out.println("[Updater] Latest version: " + latestVersion);
            this.outdated = currentVersion.shouldUpdateTo(latestVersion);
        }
        catch (final Exception e) {
            System.err.println("[Updater] An error occurred!");
            e.printStackTrace();
        }
        if (latestVersion == null || latestVersion.isInvalid()) {
            this.component = new TextComponentString("An error occurred while checking for updates. Click §nhere§r to check manually.");
            final ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.wurstclient.net/download/");
            this.component.getStyle().setClickEvent(event);
            WurstClient.INSTANCE.events.add(UpdateListener.class, this);
            return;
        }
        if (!latestVersion.isHigherThan("6.35.3")) {
            return;
        }
        this.component = new TextComponentString("Wurst " + latestVersion + " is now available." + " Click §nhere§r to download the update.");
        final ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.wurstclient.net/download/");
        this.component.getStyle().setClickEvent(event);
        WurstClient.INSTANCE.events.add(UpdateListener.class, this);
    }
    
    private boolean containsCompatibleAsset(final JsonArray assets) {
        for (final JsonElement asset : assets) {
            if (!asset.getAsJsonObject().get("name").getAsString().endsWith("MC1.12-OF.jar")) {
                continue;
            }
            return true;
        }
        return false;
    }
    
    private JsonElement fetchJson(final String url) throws IOException {
        final URI u = URI.create(url);
        Throwable t = null;
        try {
            final InputStream in = u.toURL().openStream();
            try {
                return new JsonParser().parse((Reader)new BufferedReader(new InputStreamReader(in)));
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        finally {
            if (t == null) {
                final Throwable exception;
                t = exception;
            }
            else {
                final Throwable exception;
                if (t != exception) {
                    t.addSuppressed(exception);
                }
            }
        }
    }
    
    public void update() {
        new Thread(() -> {
            try {
                final Path path = Paths.get(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent().resolve("Wurst-updater.jar");
                try {
                    final InputStream in = this.getClass().getClassLoader().getResourceAsStream("assets/minecraft/wurst/Wurst-updater.jar");
                    try {
                        Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                    finally {
                        if (in != null) {
                            in.close();
                        }
                    }
                }
                finally {
                    Throwable t = null;
                    if (t == null) {
                        final Throwable exception;
                        t = exception;
                    }
                    else {
                        final Throwable exception;
                        if (t != exception) {
                            t.addSuppressed(exception);
                        }
                    }
                }
                new ProcessBuilder(new String[] { "cmd.exe", "/c", "java", "-jar", path.toString(), "update", path.getParent().toString(), this.latestVersionString, "1.12-OF" });
                final ProcessBuilder processBuilder;
                final ProcessBuilder pb = processBuilder;
                pb.redirectErrorStream(true);
                final Process p = pb.start();
                try {
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
                    final BufferedReader bufferedReader;
                    final BufferedReader reader = bufferedReader;
                    try {
                        Label_0245_1: {
                            break Label_0245_1;
                            String s;
                            do {
                                final String line;
                                System.out.println(line);
                                s = (line = reader.readLine());
                            } while (s != null);
                        }
                    }
                    finally {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                }
                finally {
                    Throwable t2 = null;
                    if (t2 == null) {
                        final Throwable exception2;
                        t2 = exception2;
                    }
                    else {
                        final Throwable exception2;
                        if (t2 != exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                }
            }
            catch (final Exception e) {
                System.err.println("Could not update!");
                e.printStackTrace();
            }
        }).start();
    }
    
    public boolean isOutdated() {
        return this.outdated;
    }
    
    public String getLatestVersion() {
        return this.latestVersionString;
    }
}
