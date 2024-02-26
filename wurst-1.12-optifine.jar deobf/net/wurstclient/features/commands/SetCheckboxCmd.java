// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
@HelpPage("Commands/setcheckbox")
public final class SetCheckboxCmd extends Command
{
    public SetCheckboxCmd() {
        super("setcheckbox", "Changes a checkbox setting of a feature. Allows you\nto toggle checkboxes through keybinds.", new String[] { "<feature> <checkbox_setting> (on|off|toggle)" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 3) {
            throw new CmdSyntaxError();
        }
        Feature feature = null;
        final String featureName = args[0];
        for (final Feature item : SetCheckboxCmd.WURST.navigator) {
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
        if (!(setting instanceof CheckboxSetting)) {
            throw new CmdError(String.valueOf(feature.getName()) + " " + setting.getName() + " is not a checkbox setting.");
        }
        final CheckboxSetting checkboxSetting = (CheckboxSetting)setting;
        final String valueName = args[2];
        if (valueName.equalsIgnoreCase("on")) {
            checkboxSetting.setChecked(true);
        }
        else if (valueName.equalsIgnoreCase("off")) {
            checkboxSetting.setChecked(false);
        }
        else {
            if (!valueName.equalsIgnoreCase("toggle")) {
                throw new CmdSyntaxError();
            }
            checkboxSetting.toggle();
        }
    }
}
