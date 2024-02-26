// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.MathUtils;
import java.util.ArrayList;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.settings.Setting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/friends")
public final class FriendsCmd extends Command
{
    private static final int FRIENDS_PER_PAGE = 8;
    private final CheckboxSetting middleClickFriends;
    
    public FriendsCmd() {
        super("friends", "Manages your friends list.", new String[] { "add <name>", "remove <name>", "remove-all", "list [<page>]" });
        this.addSetting(this.middleClickFriends = new CheckboxSetting("Middle click friends", "Add/remove friends by clicking them with\nthe middle mouse button.", true));
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length < 1 || args.length > 2) {
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
            case "remove-all": {
                this.removeAll(args);
                return;
            }
            default:
                break;
        }
        throw new CmdSyntaxError();
    }
    
    private void add(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        final String name = args[1];
        if (FriendsCmd.WURST.friends.contains(name)) {
            throw new CmdError("\"" + name + "\" is already in your friends list.");
        }
        FriendsCmd.WURST.friends.addAndSave(name);
        ChatUtils.message("Added friend \"" + name + "\".");
    }
    
    private void remove(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        final String name = args[1];
        if (!FriendsCmd.WURST.friends.contains(name)) {
            throw new CmdError("\"" + name + "\" is not in your friends list.");
        }
        FriendsCmd.WURST.friends.removeAndSave(name);
        ChatUtils.message("Removed friend \"" + name + "\".");
    }
    
    private void removeAll(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        FriendsCmd.WURST.friends.removeAllAndSave();
        ChatUtils.message("All friends removed. Oof.");
    }
    
    private void list(final String[] args) throws CmdException {
        if (args.length > 2) {
            throw new CmdSyntaxError();
        }
        final ArrayList<String> friends = FriendsCmd.WURST.friends.toList();
        final int page = this.parsePage(args);
        int pages = (int)Math.ceil(friends.size() / 8.0);
        pages = Math.max(pages, 1);
        if (page > pages || page < 1) {
            throw new CmdSyntaxError("Invalid page: " + page);
        }
        ChatUtils.message("Current friends: " + friends.size());
        final int start = (page - 1) * 8;
        final int end = Math.min(page * 8, friends.size());
        ChatUtils.message("Friends list (page " + page + "/" + pages + ")");
        for (int i = start; i < end; ++i) {
            ChatUtils.message(friends.get(i).toString());
        }
    }
    
    private int parsePage(final String[] args) throws CmdSyntaxError {
        if (args.length < 2) {
            return 1;
        }
        if (!MathUtils.isInteger(args[1])) {
            throw new CmdSyntaxError("Not a number: " + args[1]);
        }
        return Integer.parseInt(args[1]);
    }
    
    public CheckboxSetting getMiddleClickFriends() {
        return this.middleClickFriends;
    }
}
