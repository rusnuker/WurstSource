// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Collection;
import net.minecraft.crash.CrashReportCategory;
import java.util.Arrays;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.WurstClient;
import java.lang.reflect.Field;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import java.util.Comparator;
import net.wurstclient.features.Command;
import java.util.TreeMap;
import net.wurstclient.events.ChatOutputListener;

public final class CmdManager implements ChatOutputListener
{
    private final TreeMap<String, Command> cmds;
    public final AddAltCmd addAltCmd;
    public final AnnoyCmd annoyCmd;
    public final AuthorCmd authorCmd;
    public final BindsCmd bindsCmd;
    public final BlinkCmd blinkCmd;
    public final ClearCmd clearCmd;
    public final CopyItemCmd copyitemCmd;
    public final DamageCmd damageCmd;
    public final DropCmd dropCmd;
    public final EnchantCmd enchantCmd;
    public final ExcavateCmd excavateCmd;
    public final FeaturesCmd featuresCmd;
    public final FollowCmd followCmd;
    public final FriendsCmd friendsCmd;
    public final GetPosCmd getPosCmd;
    public final GiveCmd giveCmd;
    public final GmCmd gmCmd;
    public final GoToCmd goToCmd;
    public final HelpCmd HhelpCmd;
    public final InvseeCmd invseeCmd;
    public final IpCmd ipCmd;
    public final JumpCmd jumpCmd;
    public final LeaveCmd leaveCmd;
    public final ModifyCmd modifyCmd;
    public final NothingCmd nothingCmd;
    public final NukerCmd nukerCmd;
    public final PathCmd pathCmd;
    public final PotionCmd potionCmd;
    public final ProtectCmd protectCmd;
    public final RenameCmd renameCmd;
    public final RepairCmd repairCmd;
    public final RvCmd rvCmd;
    public final SvCmd svCmd;
    public final SayCmd sayCmd;
    public final SetCheckboxCmd setCheckboxCmd;
    public final SetModeCmd setModeCmd;
    public final SetSliderCmd setSliderCmd;
    public final SpammerCmd spammerCmd;
    public final TacoCmd tacoCmd;
    public final TCmd tCmd;
    public final ThrowCmd throwCmd;
    public final TpCmd tpCmd;
    public final VClipCmd vClipCmd;
    public final ViewNbtCmd viewNbtCmd;
    public final WmsCmd wmsCmd;
    public final XRayCmd xRayCmd;
    
    public CmdManager() {
        this.cmds = new TreeMap<String, Command>(new Comparator<String>() {
            @Override
            public int compare(final String o1, final String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        this.addAltCmd = new AddAltCmd();
        this.annoyCmd = new AnnoyCmd();
        this.authorCmd = new AuthorCmd();
        this.bindsCmd = new BindsCmd();
        this.blinkCmd = new BlinkCmd();
        this.clearCmd = new ClearCmd();
        this.copyitemCmd = new CopyItemCmd();
        this.damageCmd = new DamageCmd();
        this.dropCmd = new DropCmd();
        this.enchantCmd = new EnchantCmd();
        this.excavateCmd = new ExcavateCmd();
        this.featuresCmd = new FeaturesCmd();
        this.followCmd = new FollowCmd();
        this.friendsCmd = new FriendsCmd();
        this.getPosCmd = new GetPosCmd();
        this.giveCmd = new GiveCmd();
        this.gmCmd = new GmCmd();
        this.goToCmd = new GoToCmd();
        this.HhelpCmd = new HelpCmd();
        this.invseeCmd = new InvseeCmd();
        this.ipCmd = new IpCmd();
        this.jumpCmd = new JumpCmd();
        this.leaveCmd = new LeaveCmd();
        this.modifyCmd = new ModifyCmd();
        this.nothingCmd = new NothingCmd();
        this.nukerCmd = new NukerCmd();
        this.pathCmd = new PathCmd();
        this.potionCmd = new PotionCmd();
        this.protectCmd = new ProtectCmd();
        this.renameCmd = new RenameCmd();
        this.repairCmd = new RepairCmd();
        this.rvCmd = new RvCmd();
        this.svCmd = new SvCmd();
        this.sayCmd = new SayCmd();
        this.setCheckboxCmd = new SetCheckboxCmd();
        this.setModeCmd = new SetModeCmd();
        this.setSliderCmd = new SetSliderCmd();
        this.spammerCmd = new SpammerCmd();
        this.tacoCmd = new TacoCmd();
        this.tCmd = new TCmd();
        this.throwCmd = new ThrowCmd();
        this.tpCmd = new TpCmd();
        this.vClipCmd = new VClipCmd();
        this.viewNbtCmd = new ViewNbtCmd();
        this.wmsCmd = new WmsCmd();
        this.xRayCmd = new XRayCmd();
        try {
            Field[] fields;
            for (int length = (fields = CmdManager.class.getFields()).length, i = 0; i < length; ++i) {
                final Field field = fields[i];
                if (field.getName().endsWith("Cmd")) {
                    final Command cmd = (Command)field.get(this);
                    this.cmds.put(cmd.getCmdName(), cmd);
                }
            }
        }
        catch (final Exception e) {
            throw new ReportedException(CrashReport.makeCrashReport(e, "Initializing Wurst commands"));
        }
    }
    
    @Override
    public void onSentMessage(final ChatOutputEvent event) {
        if (!WurstClient.INSTANCE.isEnabled()) {
            return;
        }
        final String message = event.getMessage().trim();
        if (!message.startsWith(".")) {
            return;
        }
        event.cancel();
        this.runCommand(message.substring(1));
    }
    
    public void runCommand(final String input) {
        final String[] parts = input.split(" ");
        final Command cmd = this.getCommandByName(parts[0]);
        if (cmd == null) {
            ChatUtils.error("Unknown command: ." + parts[0]);
            if (input.startsWith("/")) {
                ChatUtils.message("Use \".say " + input + "\" to send it as a chat command.");
            }
            else {
                ChatUtils.message("Type \".help\" for a list of commands or \".say ." + input + "\" to send it as a chat message.");
            }
            return;
        }
        try {
            cmd.call(Arrays.copyOfRange(parts, 1, parts.length));
        }
        catch (final Command.CmdException e) {
            e.printToChat();
        }
        catch (final Throwable e2) {
            final CrashReport crashReport = CrashReport.makeCrashReport(e2, "Running Wurst command");
            final CrashReportCategory crashReportCategory = crashReport.makeCategory("Affected command");
            crashReportCategory.setDetail("Command input", () -> s);
            throw new ReportedException(crashReport);
        }
    }
    
    public Command getCommandByName(final String name) {
        return this.cmds.get(name);
    }
    
    public Collection<Command> getAllCommands() {
        return this.cmds.values();
    }
    
    public int countCommands() {
        return this.cmds.size();
    }
}
