// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.gui;

import net.wurstclient.font.Fonts;
import java.util.Comparator;
import net.wurstclient.features.special_features.YesCheatSpf;
import net.minecraft.client.Minecraft;
import java.util.Iterator;
import net.wurstclient.features.mods.other.NavigatorMod;
import net.wurstclient.features.Hack;
import net.wurstclient.WurstClient;
import net.minecraft.client.gui.ScaledResolution;
import net.wurstclient.features.special_features.HackListSpf;
import java.util.ArrayList;
import net.wurstclient.events.UpdateListener;

public final class HackList implements UpdateListener
{
    private final ArrayList<Entry> activeHacks;
    private final HackListSpf hackListSpf;
    private int posY;
    private ScaledResolution sr;
    private int textColor;
    
    public HackList() {
        this.activeHacks = new ArrayList<Entry>();
        this.hackListSpf = WurstClient.INSTANCE.special.hackListSpf;
        for (final Hack hack : WurstClient.INSTANCE.hax.getAll()) {
            if (hack instanceof NavigatorMod) {
                continue;
            }
            if (!hack.isActive()) {
                continue;
            }
            this.activeHacks.add(new Entry(hack, 0));
        }
        WurstClient.INSTANCE.events.add(UpdateListener.class, this);
    }
    
    public void render(final float partialTicks) {
        if (this.hackListSpf.isHidden()) {
            return;
        }
        if (this.hackListSpf.isPositionRight() || !WurstClient.INSTANCE.special.wurstLogoSpf.isVisible()) {
            this.posY = 0;
        }
        else {
            this.posY = 19;
        }
        this.sr = new ScaledResolution(Minecraft.getMinecraft());
        if (WurstClient.INSTANCE.hax.rainbowUiMod.isActive()) {
            final float[] acColor = WurstClient.INSTANCE.getGui().getAcColor();
            this.textColor = (0x4000000 | (int)(acColor[0] * 256.0f) << 16 | (int)(acColor[1] * 256.0f) << 8 | (int)(acColor[2] * 256.0f));
        }
        else {
            this.textColor = 83886079;
        }
        final YesCheatSpf yesCheatSpf = WurstClient.INSTANCE.special.yesCheatSpf;
        if (yesCheatSpf.modeIndicator.isChecked()) {
            this.drawString("YesCheat+: " + yesCheatSpf.getProfile().getName());
        }
        final int height = this.posY + this.activeHacks.size() * 9;
        if (this.hackListSpf.isCountMode() || height > this.sr.getScaledHeight()) {
            this.drawString((this.activeHacks.size() == 1) ? "1 hack active" : (String.valueOf(this.activeHacks.size()) + " hacks active"));
        }
        else if (this.hackListSpf.isAnimations()) {
            for (final Entry e : this.activeHacks) {
                this.drawWithOffset(e, partialTicks);
            }
        }
        else {
            for (final Entry e : this.activeHacks) {
                this.drawString(e.hack.getRenderName());
            }
        }
    }
    
    public void updateState(final Hack hack) {
        if (hack.isActive()) {
            final Iterator<Entry> iterator = this.activeHacks.iterator();
            Entry e = null;
            while (iterator.hasNext()) {
                e = iterator.next();
                if (e.hack == hack) {
                    return;
                }
            }
            this.activeHacks.add(new Entry(hack, 4));
            this.activeHacks.sort(Comparator.comparing(e -> e.hack.getName()));
        }
        else if (!this.hackListSpf.isAnimations()) {
            final Entry e;
            this.activeHacks.removeIf(e -> e.hack == hack2);
        }
    }
    
    @Override
    public void onUpdate() {
        if (!this.hackListSpf.isAnimations()) {
            return;
        }
        final Iterator<Entry> itr = this.activeHacks.iterator();
        while (itr.hasNext()) {
            final Entry e = itr.next();
            if (e.hack.isActive()) {
                Entry.access$2(e, e.offset);
                if (e.offset <= 0) {
                    continue;
                }
                final Entry entry = e;
                Entry.access$3(entry, entry.offset - 1);
            }
            else if (!e.hack.isActive() && e.offset < 4) {
                Entry.access$2(e, e.offset);
                final Entry entry2 = e;
                Entry.access$3(entry2, entry2.offset + 1);
            }
            else {
                if (e.hack.isActive() || e.offset != 4) {
                    continue;
                }
                itr.remove();
            }
        }
    }
    
    private void drawString(final String s) {
        int posX;
        if (this.hackListSpf.isPositionRight()) {
            posX = this.sr.getScaledWidth() - Fonts.segoe18.getStringWidth(s) - 2;
        }
        else {
            posX = 2;
        }
        Fonts.segoe18.drawString(s, posX + 1, this.posY + 1, -16777216);
        Fonts.segoe18.drawString(s, posX, this.posY, this.textColor | 0xFF000000);
        this.posY += 9;
    }
    
    private void drawWithOffset(final Entry e, final float partialTicks) {
        final String s = e.hack.getRenderName();
        final float offset = e.offset * partialTicks + e.prevOffset * (1.0f - partialTicks);
        float posX;
        if (this.hackListSpf.isPositionRight()) {
            posX = this.sr.getScaledWidth() - Fonts.segoe18.getStringWidth(s) - 2 + 5.0f * offset;
        }
        else {
            posX = 2.0f - 5.0f * offset;
        }
        final int alpha = (int)(255.0f * (1.0f - offset / 4.0f)) << 24;
        Fonts.segoe18.drawString(s, posX + 1.0f, (float)(this.posY + 1), 0x4000000 | alpha);
        Fonts.segoe18.drawString(s, posX, (float)this.posY, this.textColor | alpha);
        this.posY += 9;
    }
    
    private static final class Entry
    {
        private final Hack hack;
        private int offset;
        private int prevOffset;
        
        public Entry(final Hack hack, final int offset) {
            this.hack = hack;
            this.offset = offset;
            this.prevOffset = offset;
        }
        
        static /* synthetic */ void access$2(final Entry entry, final int prevOffset) {
            entry.prevOffset = prevOffset;
        }
        
        static /* synthetic */ void access$3(final Entry entry, final int offset) {
            entry.offset = offset;
        }
    }
}
