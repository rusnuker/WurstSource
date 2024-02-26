// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.keybinds.KeybindList;
import net.wurstclient.util.MathUtils;
import java.util.Arrays;
import org.lwjgl.input.Keyboard;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
public final class BindsCmd extends Command
{
    public BindsCmd() {
        super("binds", "Allows you to manage keybinds through the chat.\nMultiple hacks/commands must be separated by ';'.", new String[] { "add <key> <hacks>", "add <key> <commands>", "remove <key>", "list [<page>]", "remove-all", "reset" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length < 1) {
            throw new CmdSyntaxError();
        }
        final String lowerCase;
        switch (lowerCase = args[0].toLowerCase()) {
            case "remove": {
                this.remove(args);
                return;
            }
            case "add": {
                this.add(args);
                return;
            }
            case "list": {
                this.list(args);
                return;
            }
            case "reset": {
                BindsCmd.WURST.getKeybinds().loadDefaults();
                ChatUtils.message("All keybinds reset to defaults.");
                return;
            }
            case "remove-all": {
                BindsCmd.WURST.getKeybinds().removeAll();
                ChatUtils.message("All keybinds removed.");
                return;
            }
            default:
                break;
        }
        throw new CmdSyntaxError();
    }
    
    private void add(final String[] args) throws CmdException {
        if (args.length < 3) {
            throw new CmdSyntaxError();
        }
        final String key = args[1].toUpperCase();
        if (Keyboard.getKeyIndex(key) == 0) {
            throw new CmdSyntaxError("Unknown key: " + key);
        }
        final String commands = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 2, args.length));
        BindsCmd.WURST.getKeybinds().add(key, commands);
        ChatUtils.message("Keybind set: " + key + " -> " + commands);
    }
    
    private void remove(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        final String key = args[1].toUpperCase();
        if (Keyboard.getKeyIndex(key) == 0) {
            throw new CmdSyntaxError("Unknown key: " + key);
        }
        final String oldCommands = BindsCmd.WURST.getKeybinds().getCommands(key);
        if (oldCommands == null) {
            throw new CmdError("Nothing to remove.");
        }
        BindsCmd.WURST.getKeybinds().remove(key);
        ChatUtils.message("Keybind removed: " + key + " -> " + oldCommands);
    }
    
    private void list(final String[] args) throws CmdException {
        if (args.length > 2) {
            throw new CmdSyntaxError();
        }
        int page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            if (!MathUtils.isInteger(args[1])) {
                throw new CmdSyntaxError("Not a number: " + args[1]);
            }
            page = Integer.parseInt(args[1]);
        }
        final int keybinds = BindsCmd.WURST.getKeybinds().size();
        final int pages = Math.max((int)Math.ceil(keybinds / 8.0), 1);
        if (page > pages || page < 1) {
            throw new CmdSyntaxError("Invalid page: " + page);
        }
        ChatUtils.message("Total: " + keybinds + ((keybinds == 1) ? " keybind" : " keybinds"));
        ChatUtils.message("Keybind list (page " + page + "/" + pages + ")");
        for (int i = (page - 1) * 8; i < Math.min(page * 8, keybinds); ++i) {
            final KeybindList.Keybind k = BindsCmd.WURST.getKeybinds().get(i);
            ChatUtils.message(String.valueOf(k.getKey()) + " -> " + k.getCommands());
        }
    }
}
