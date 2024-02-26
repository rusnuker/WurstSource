// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import net.wurstclient.util.MathUtils;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.util.ChatUtils;
import net.minecraft.network.Packet;
import net.wurstclient.compatibility.WConnection;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/modify")
public final class ModifyCmd extends Command
{
    public ModifyCmd() {
        super("modify", "Modifies items in creative mode.", new String[] { "add <nbt_data>", "set <nbt_data>", "remove <nbt_path>", "metadata <value>" });
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        final EntityPlayerSP player = ModifyCmd.MC.player;
        if (!player.abilities.creativeMode) {
            throw new CmdError("Creative mode only.");
        }
        if (args.length < 2) {
            throw new CmdSyntaxError();
        }
        final ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            throw new CmdError("You must hold an item in your main hand.");
        }
        Label_0217: {
            final String lowerCase;
            switch (lowerCase = args[0].toLowerCase()) {
                case "remove": {
                    this.remove(stack, args);
                    break Label_0217;
                }
                case "metadata": {
                    this.metadata(stack, args);
                    break Label_0217;
                }
                case "add": {
                    this.add(stack, args);
                    break Label_0217;
                }
                case "set": {
                    this.set(stack, args);
                    break Label_0217;
                }
                default:
                    break;
            }
            throw new CmdSyntaxError();
        }
        WConnection.sendPacket(new CPacketCreativeInventoryAction(36 + player.inventory.selectedSlot, stack));
        ChatUtils.message("Item modified.");
    }
    
    private void add(final ItemStack stack, final String[] args) throws CmdError {
        String nbt = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 1, args.length));
        nbt = nbt.replace("$", "§").replace("§§", "$");
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        try {
            final NBTTagCompound tag = JsonToNBT.getTagFromJson(nbt);
            stack.getTagCompound().merge(tag);
        }
        catch (final NBTException e) {
            ChatUtils.message(e.getMessage());
            throw new CmdError("NBT data is invalid.");
        }
    }
    
    private void set(final ItemStack stack, final String[] args) throws CmdError {
        String nbt = String.join(" ", (CharSequence[])Arrays.copyOfRange(args, 1, args.length));
        nbt = nbt.replace("$", "§").replace("§§", "$");
        try {
            final NBTTagCompound tag = JsonToNBT.getTagFromJson(nbt);
            stack.setTagCompound(tag);
        }
        catch (final NBTException e) {
            ChatUtils.message(e.getMessage());
            throw new CmdError("NBT data is invalid.");
        }
    }
    
    private void remove(final ItemStack stack, final String[] args) throws CmdException {
        if (args.length > 2) {
            throw new CmdSyntaxError();
        }
        final NbtPath path = this.parseNbtPath(stack.getTagCompound(), args[1]);
        if (path == null) {
            throw new CmdError("The path does not exist.");
        }
        path.base.removeTag(path.key);
    }
    
    private void metadata(final ItemStack stack, final String[] args) throws CmdException {
        if (args.length > 2) {
            throw new CmdSyntaxError();
        }
        if (!MathUtils.isInteger(args[1])) {
            throw new CmdSyntaxError("Value must be a number.");
        }
        stack.setItemDamage(Integer.parseInt(args[1]));
    }
    
    private NbtPath parseNbtPath(final NBTTagCompound tag, final String path) {
        final String[] parts = path.split("\\.");
        NBTTagCompound base = tag;
        if (base == null) {
            return null;
        }
        for (int i = 0; i < parts.length - 1; ++i) {
            final String part = parts[i];
            if (!base.hasKey(part) || !(base.getTag(part) instanceof NBTTagCompound)) {
                return null;
            }
            base = base.getCompoundTag(part);
        }
        if (!base.hasKey(parts[parts.length - 1])) {
            return null;
        }
        return new NbtPath(base, parts[parts.length - 1]);
    }
    
    private static class NbtPath
    {
        public NBTTagCompound base;
        public String key;
        
        public NbtPath(final NBTTagCompound base, final String key) {
            this.base = base;
            this.key = key;
        }
    }
}
