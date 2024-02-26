// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import net.wurstclient.util.MathUtils;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.Setting;
import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.DontBlock;
import net.wurstclient.features.Command;

@DontBlock
@HelpPage("Commands/setslider")
public final class SetSliderCmd extends Command
{
    public SetSliderCmd() {
        super("setslider", "Changes a slider setting of a feature. Allows you to\nmove sliders through keybinds.", new String[] { "<feature> <slider_setting> (<value>|more|less)" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length != 3) {
            throw new CmdSyntaxError();
        }
        Feature feature = null;
        final String featureName = args[0];
        for (final Feature item : SetSliderCmd.WURST.navigator) {
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
        if (!(setting instanceof SliderSetting)) {
            throw new CmdError(String.valueOf(feature.getName()) + " " + setting.getName() + " is not a slider setting.");
        }
        final SliderSetting sliderSetting = (SliderSetting)setting;
        final String valueName = args[2];
        if (valueName.equalsIgnoreCase("more")) {
            sliderSetting.increaseValue();
        }
        else if (valueName.equalsIgnoreCase("less")) {
            sliderSetting.decreaseValue();
        }
        else {
            if (!MathUtils.isDouble(valueName)) {
                throw new CmdSyntaxError("Value must be a number.");
            }
            final double value = Double.parseDouble(valueName);
            sliderSetting.setValue(value);
        }
    }
}
