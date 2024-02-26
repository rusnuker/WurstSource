// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.wurstclient.util.MathUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.wurstclient.compatibility.WPotion;
import net.minecraft.nbt.NBTTagList;
import net.wurstclient.compatibility.WItem;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/potion")
public final class PotionCmd extends Command
{
    public PotionCmd() {
        super("potion", "Changes the effects of the held potion.", new String[] { "add (<effect> <amplifier> <duration>)...", "set (<effect> <amplifier> <duration>)...", "remove <effect>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        if (!PotionCmd.MC.player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        final ItemStack currentItem = PotionCmd.MC.player.inventory.getCurrentItem();
        if (!WItem.isPotion(currentItem)) {
            throw new CmdError("You are not holding a potion in your hand.");
        }
        final NBTTagList newEffects = new NBTTagList();
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                throw new CmdSyntaxError();
            }
            int id = 0;
            id = this.parsePotionEffectId(args[1]);
            final List<PotionEffect> oldEffects = WPotion.getEffectsFromStack(currentItem);
            if (oldEffects != null) {
                for (final PotionEffect temp : oldEffects) {
                    if (WPotion.getIdFromEffect(temp) != id) {
                        final NBTTagCompound effect = new NBTTagCompound();
                        effect.setInteger("Id", WPotion.getIdFromEffect(temp));
                        effect.setInteger("Amplifier", temp.getAmplifier());
                        effect.setInteger("Duration", temp.getDuration());
                        newEffects.appendTag(effect);
                    }
                }
            }
            currentItem.setTagInfo("CustomPotionEffects", newEffects);
        }
        else {
            if ((args.length - 1) % 3 != 0) {
                throw new CmdSyntaxError();
            }
            if (args[0].equalsIgnoreCase("add")) {
                final List<PotionEffect> oldEffects2 = WPotion.getEffectsFromStack(currentItem);
                if (oldEffects2 != null) {
                    for (final PotionEffect temp2 : oldEffects2) {
                        final NBTTagCompound effect2 = new NBTTagCompound();
                        effect2.setInteger("Id", WPotion.getIdFromEffect(temp2));
                        effect2.setInteger("Amplifier", temp2.getAmplifier());
                        effect2.setInteger("Duration", temp2.getDuration());
                        newEffects.appendTag(effect2);
                    }
                }
            }
            else if (!args[0].equalsIgnoreCase("set")) {
                throw new CmdSyntaxError();
            }
            for (int i = 0; i < (args.length - 1) / 3; ++i) {
                final int id2 = this.parsePotionEffectId(args[1 + i * 3]);
                int amplifier = 0;
                int duration = 0;
                if (!MathUtils.isInteger(args[2 + i * 3]) || !MathUtils.isInteger(args[3 + i * 3])) {
                    throw new CmdSyntaxError();
                }
                amplifier = Integer.parseInt(args[2 + i * 3]) - 1;
                duration = Integer.parseInt(args[3 + i * 3]);
                final NBTTagCompound effect = new NBTTagCompound();
                effect.setInteger("Id", id2);
                effect.setInteger("Amplifier", amplifier);
                effect.setInteger("Duration", duration * 20);
                newEffects.appendTag(effect);
            }
            System.out.println(newEffects);
            currentItem.setTagInfo("CustomPotionEffects", newEffects);
        }
    }
    
    public int parsePotionEffectId(final String input) throws CmdSyntaxError {
        int id = 0;
        try {
            id = Integer.parseInt(input);
        }
        catch (final NumberFormatException var11) {
            try {
                id = WPotion.getIdFromResourceLocation(input);
            }
            catch (final NullPointerException e) {
                throw new CmdSyntaxError();
            }
        }
        if (id < 1) {
            throw new CmdSyntaxError();
        }
        return id;
    }
}
