// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.keybinds;

import net.wurstclient.features.Hack;
import net.wurstclient.events.KeyPressListener;
import org.lwjgl.input.Keyboard;
import net.wurstclient.WurstClient;
import net.wurstclient.features.commands.CmdManager;
import net.wurstclient.features.mods.LocalHackList;

public final class KeybindProcessor
{
    private final LocalHackList mods;
    private final KeybindList keybinds;
    private final CmdManager cmdProcessor;
    
    public KeybindProcessor(final LocalHackList hax, final KeybindList keybinds, final CmdManager cmdProcessor) {
        this.mods = hax;
        this.keybinds = keybinds;
        this.cmdProcessor = cmdProcessor;
    }
    
    public void onKeyPress() {
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        final int keyCode = Keyboard.getEventKey();
        if (keyCode == 0 || !Keyboard.getEventKeyState()) {
            return;
        }
        final String keyName = Keyboard.getKeyName(keyCode);
        final KeyPressListener.KeyPressEvent event = new KeyPressListener.KeyPressEvent(keyCode, keyName);
        WurstClient.INSTANCE.events.fire(event);
        String commands = this.keybinds.getCommands(keyName);
        if (commands == null) {
            return;
        }
        commands = commands.replace(";", "§").replace("§§", ";");
        String[] split;
        for (int length = (split = commands.split("§")).length, i = 0; i < length; ++i) {
            String command = split[i];
            command = command.trim();
            if (command.startsWith(".")) {
                this.cmdProcessor.runCommand(command.substring(1));
            }
            else if (command.contains(" ")) {
                this.cmdProcessor.runCommand(command);
            }
            else {
                final Hack mod = this.mods.getByName(command);
                if (mod != null) {
                    mod.toggle();
                }
                else {
                    this.cmdProcessor.runCommand(command);
                }
            }
        }
    }
}
