// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import net.wurstclient.settings.ModeSetting;
import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
@HelpPage("Commands/setmode")
public final class SetModeCmd extends Command
{
    public SetModeCmd() {
        super("setmode", "Changes a mode setting of a feature. Allows you to\nswitch modes through keybinds.", new String[] { "<feature> <mode_setting> (<mode>|next|prev)" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 3) {
            throw new CmdSyntaxError();
        }
        Feature feature = null;
        final String featureName = args[0];
        for (final Feature item : SetModeCmd.WURST.navigator) {
            if (featureName.equalsIgnoreCase(item.getName())) {
                feature = item;
                break;
            }
        }
        if (feature == null) {
            throw new CmdError("A feature named \"" + featureName + "\" could not be found.");
        }
        Setting setting = null;
        final String settingName = args[1].replace("_", " ");
        for (final Setting featureSetting : feature.getSettings()) {
            if (featureSetting.getName().equalsIgnoreCase(settingName)) {
                setting = featureSetting;
                break;
            }
        }
        if (setting == null) {
            throw new CmdError("A setting named \"" + settingName + "\" could not be found in " + feature.getName() + ".");
        }
        if (!(setting instanceof ModeSetting)) {
            throw new CmdError(String.valueOf(feature.getName()) + " " + setting.getName() + " is not a mode setting.");
        }
        final ModeSetting modeSetting = (ModeSetting)setting;
        final String modeName = args[2].replace("_", " ");
        if (modeName.equalsIgnoreCase("next")) {
            modeSetting.nextMode();
        }
        else if (modeName.equalsIgnoreCase("prev")) {
            modeSetting.prevMode();
        }
        else {
            final int mode = modeSetting.indexOf(modeName);
            if (mode == -1) {
                throw new CmdError("A " + feature.getName() + " " + setting.getName() + " named \"" + modeName + "\" could not be found.");
            }
            modeSetting.setSelected(mode);
        }
    }
}
