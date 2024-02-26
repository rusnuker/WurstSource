// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.settings;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.wurstclient.clickgui.components.Slider;
import net.wurstclient.clickgui.Component;
import net.wurstclient.WurstClient;
import net.wurstclient.util.MathUtils;
import net.wurstclient.compatibility.WMath;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;
import net.wurstclient.navigator.NavigatorFeatureScreen;

public class SliderSetting extends Setting implements SliderLock
{
    private double value;
    private final double defaultValue;
    private final double minimum;
    private final double maximum;
    private double usableMin;
    private double usableMax;
    private final double increment;
    private final ValueDisplay display;
    private SliderLock lock;
    private boolean disabled;
    private int y;
    
    public SliderSetting(final String name, final String description, final double value, final double minimum, final double maximum, final double increment, final ValueDisplay display) {
        super(name, description);
        this.value = value;
        this.defaultValue = value;
        this.minimum = minimum;
        this.maximum = maximum;
        this.usableMin = minimum;
        this.usableMax = maximum;
        this.increment = increment;
        this.display = display;
    }
    
    public SliderSetting(final String name, final double value, final double minimum, final double maximum, final double increment, final ValueDisplay display) {
        this(name, null, value, minimum, maximum, increment, display);
    }
    
    @Override
    public final void addToFeatureScreen(final NavigatorFeatureScreen featureScreen) {
        featureScreen.addText("\n" + this.getName() + ":");
        this.y = 60 + featureScreen.getTextHeight();
        featureScreen.addText("\n");
        featureScreen.addSlider(this);
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds(final String featureName) {
        final ArrayList<PossibleKeybind> binds = new ArrayList<PossibleKeybind>();
        final String fullName = String.valueOf(featureName) + " " + this.getName();
        final String cmd = ".setslider " + featureName.toLowerCase() + " " + this.getName().toLowerCase().replace(" ", "_") + " ";
        binds.add(new PossibleKeybind(String.valueOf(cmd) + "more", "Increase " + fullName));
        binds.add(new PossibleKeybind(String.valueOf(cmd) + "less", "Decrease " + fullName));
        return binds;
    }
    
    @Override
    public final double getValue() {
        return WMath.clamp(this.isLocked() ? this.lock.getValue() : this.value, this.usableMin, this.usableMax);
    }
    
    public final float getValueF() {
        return (float)this.getValue();
    }
    
    public final int getValueI() {
        return (int)this.getValue();
    }
    
    public final void setValue(final double value) {
        if (this.disabled || this.isLocked()) {
            return;
        }
        this.setValueIgnoreLock(value);
    }
    
    private void setValueIgnoreLock(double value) {
        value = (int)Math.round(value / this.increment) * this.increment;
        value = MathUtils.clamp(value, this.usableMin, this.usableMax);
        this.value = value;
        this.update();
        WurstClient.INSTANCE.saveSettings();
    }
    
    public final void increaseValue() {
        this.setValue(this.getValue() + this.increment);
    }
    
    public final void decreaseValue() {
        this.setValue(this.getValue() - this.increment);
    }
    
    public final void lock(final SliderLock lock) {
        if (lock == this) {
            throw new IllegalArgumentException("Infinite loop of locks within locks");
        }
        this.lock = lock;
        this.update();
    }
    
    public final void unlock() {
        this.lock = null;
        this.update();
    }
    
    public final boolean isLocked() {
        return this.lock != null;
    }
    
    public final String getValueString() {
        return this.display.getValueString(this.getValue());
    }
    
    public final double getDefaultValue() {
        return this.defaultValue;
    }
    
    public final double getMinimum() {
        return this.minimum;
    }
    
    public final double getMaximum() {
        return this.maximum;
    }
    
    public final double getRange() {
        return this.maximum - this.minimum;
    }
    
    public final double getIncrement() {
        return this.increment;
    }
    
    public final double getUsableMin() {
        return this.usableMin;
    }
    
    public final void setUsableMin(final double usableMin) {
        if (usableMin < this.minimum) {
            throw new IllegalArgumentException("usableMin < minimum");
        }
        this.usableMin = usableMin;
        this.update();
    }
    
    public final void resetUsableMin() {
        this.usableMin = this.minimum;
        this.update();
    }
    
    public final double getUsableMax() {
        return this.usableMax;
    }
    
    public final void setUsableMax(final double usableMax) {
        if (usableMax > this.maximum) {
            throw new IllegalArgumentException("usableMax > maximum");
        }
        this.usableMax = usableMax;
        this.update();
    }
    
    public final void resetUsableMax() {
        this.usableMax = this.maximum;
        this.update();
    }
    
    public final boolean isLimited() {
        return this.usableMax != this.maximum || this.usableMin != this.minimum;
    }
    
    public final boolean isDisabled() {
        return this.disabled;
    }
    
    public final void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }
    
    public final int getY() {
        return this.y;
    }
    
    public final float getPercentage() {
        return (float)((this.getValue() - this.minimum) / this.getRange());
    }
    
    @Override
    public final Component getComponent() {
        return new Slider(this);
    }
    
    @Override
    public final void fromJson(final JsonElement json) {
        if (!json.isJsonPrimitive()) {
            return;
        }
        final JsonPrimitive primitive = json.getAsJsonPrimitive();
        if (!primitive.isNumber()) {
            return;
        }
        double value = primitive.getAsDouble();
        if (value > this.maximum || value < this.minimum) {
            return;
        }
        value = (int)Math.round(value / this.increment) * this.increment;
        value = MathUtils.clamp(value, this.usableMin, this.usableMax);
        this.value = value;
        this.update();
    }
    
    @Override
    public final JsonElement toJson() {
        return (JsonElement)new JsonPrimitive((Number)(Math.round(this.value * 1000000.0) / 1000000.0));
    }
    
    @Override
    public final void legacyFromJson(final JsonObject json) {
        double value = this.value;
        try {
            value = json.get(this.getName()).getAsDouble();
        }
        catch (final Exception ex) {}
        if (value > this.maximum || value < this.minimum) {
            return;
        }
        if (this.disabled || this.isLocked()) {
            return;
        }
        value = (int)Math.round(value / this.increment) * this.increment;
        value = MathUtils.clamp(value, this.usableMin, this.usableMax);
        this.value = value;
        this.update();
    }
    
    public interface ValueDisplay
    {
        public static final ValueDisplay DECIMAL = v -> new StringBuilder(String.valueOf(Math.round(v * 1000000.0) / 1000000.0)).toString();
        public static final ValueDisplay INTEGER = v -> new StringBuilder(String.valueOf((int)v)).toString();
        public static final ValueDisplay PERCENTAGE = v -> String.valueOf((int)(Math.round(v * 1.0E8) / 1000000.0)) + "%";
        public static final ValueDisplay DEGREES = v -> String.valueOf((int)v) + "\ufffd";
        public static final ValueDisplay NONE = v -> "";
        
        String getValueString(final double p0);
    }
}
