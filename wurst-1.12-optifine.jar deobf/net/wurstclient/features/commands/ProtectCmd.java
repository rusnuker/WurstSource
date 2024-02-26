// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.minecraft.entity.Entity;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/protect")
public final class ProtectCmd extends Command
{
    private EntityUtils.TargetSettings targetSettings;
    
    public ProtectCmd() {
        super("protect", "Toggles Protect or makes it protect a specific entity.", new String[] { "[<entity>]" });
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
    public void call(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        if (args.length == 0) {
            ProtectCmd.WURST.hax.protectMod.toggle();
        }
        else {
            if (ProtectCmd.WURST.hax.protectMod.isEnabled()) {
                ProtectCmd.WURST.hax.protectMod.setEnabled(false);
            }
            final Entity entity = EntityUtils.getClosestEntityWithName(args[0], this.targetSettings);
            if (entity == null) {
                throw new CmdError("Entity \"" + args[0] + "\" could not be found.");
            }
            ProtectCmd.WURST.hax.protectMod.setEnabled(true);
            ProtectCmd.WURST.hax.protectMod.setFriend(entity);
        }
    }
}
