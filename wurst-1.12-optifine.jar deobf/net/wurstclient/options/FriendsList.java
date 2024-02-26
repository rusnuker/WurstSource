// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.options;

import java.util.Collection;
import java.util.ArrayList;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.features.commands.FriendsCmd;
import net.wurstclient.files.ConfigFiles;
import net.wurstclient.WurstClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import java.util.TreeSet;

public class FriendsList extends TreeSet<String>
{
    public void middleClick(final Entity entityHit) {
        if (entityHit == null || !(entityHit instanceof EntityPlayer)) {
            return;
        }
        final FriendsCmd friendsCmd = WurstClient.INSTANCE.commands.friendsCmd;
        final CheckboxSetting middleClickFriends = friendsCmd.getMiddleClickFriends();
        if (!middleClickFriends.isChecked()) {
            return;
        }
        final String name = entityHit.getName();
        if (WurstClient.INSTANCE.friends.contains(name)) {
            WurstClient.INSTANCE.friends.remove(name);
        }
        else {
            WurstClient.INSTANCE.friends.add(name);
        }
        ConfigFiles.FRIENDS.save();
    }
    
    public void addAndSave(final String name) {
        this.add(name);
        ConfigFiles.FRIENDS.save();
    }
    
    public void removeAndSave(final String name) {
        this.remove(name);
        ConfigFiles.FRIENDS.save();
    }
    
    public void removeAllAndSave() {
        this.clear();
        ConfigFiles.FRIENDS.save();
    }
    
    public ArrayList<String> toList() {
        return new ArrayList<String>(this);
    }
}
