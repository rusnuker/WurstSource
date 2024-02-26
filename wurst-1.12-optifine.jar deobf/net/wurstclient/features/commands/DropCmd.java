// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.compatibility.WItem;
import net.wurstclient.compatibility.WPlayerController;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.wurstclient.features.HelpPage;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.features.Command;

@HelpPage("Commands/drop")
public final class DropCmd extends Command implements UpdateListener
{
    private int slowModeTimer;
    private int slowModeSlotCounter;
    
    public DropCmd() {
        super("drop", "Drops all your items on the ground.", new String[] { "[slow]" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length > 1) {
            throw new CmdSyntaxError();
        }
        if (args.length < 1) {
            this.dropAllItems();
            return;
        }
        if (!args[0].equalsIgnoreCase("slow")) {
            throw new CmdSyntaxError();
        }
        this.startSlowMode();
    }
    
    private void dropAllItems() {
        if (DropCmd.WURST.special.yesCheatSpf.getProfile().ordinal() >= YesCheatSpf.Profile.OLDER_NCP.ordinal()) {
            this.startSlowMode();
            return;
        }
        for (int i = 9; i < 45; ++i) {
            WPlayerController.windowClick_THROW(i);
        }
    }
    
    private void startSlowMode() {
        this.slowModeTimer = 5;
        this.slowModeSlotCounter = 9;
        DropCmd.EVENTS.add(UpdateListener.class, this);
    }
    
    @Override
    public void onUpdate() {
        --this.slowModeTimer;
        if (this.slowModeTimer > 0) {
            return;
        }
        this.skipEmptySlots();
        WPlayerController.windowClick_THROW(this.slowModeSlotCounter);
        ++this.slowModeSlotCounter;
        this.slowModeTimer = 5;
        if (this.slowModeSlotCounter >= 45) {
            DropCmd.EVENTS.remove(UpdateListener.class, this);
        }
    }
    
    private void skipEmptySlots() {
        while (this.slowModeSlotCounter < 45) {
            int adjustedSlot = this.slowModeSlotCounter;
            if (adjustedSlot >= 36) {
                adjustedSlot -= 36;
            }
            if (!WItem.isNullOrEmpty(DropCmd.MC.player.inventory.getInvStack(adjustedSlot))) {
                break;
            }
            ++this.slowModeSlotCounter;
        }
    }
}
