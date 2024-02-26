// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import net.wurstclient.features.Feature;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.features.Command;

public final class FollowCmd extends Command
{
    private EntityUtils.TargetSettings targetSettings;
    
    public FollowCmd() {
        super("follow", "Toggles Follow or makes it target a specific entity.", new String[] { "[<entity>]" });
        this.targetSettings = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetFriends() {
                return true;
            }
            
            @Override
            public boolean targetBehindWalls() {
                return true;
            }
        };
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { FollowCmd.WURST.hax.followMod };
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        if (args.length == 0) {
            FollowCmd.WURST.hax.followMod.toggle();
        }
        else {
            if (FollowCmd.WURST.hax.followMod.isEnabled()) {
                FollowCmd.WURST.hax.followMod.setEnabled(false);
            }
            final Entity entity = EntityUtils.getClosestEntityWithName(args[0], this.targetSettings);
            if (entity == null) {
                throw new CmdError("Entity \"" + args[0] + "\" could not be found.");
            }
            FollowCmd.WURST.hax.followMod.setEntity(entity);
            FollowCmd.WURST.hax.followMod.setEnabled(true);
        }
    }
}
