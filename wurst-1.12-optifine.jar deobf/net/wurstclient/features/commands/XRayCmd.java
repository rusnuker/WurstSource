// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.commands;

import java.util.List;
import net.wurstclient.util.MathUtils;
import net.wurstclient.features.mods.render.XRayMod;
import net.wurstclient.compatibility.WBlock;
import net.minecraft.block.Block;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.features.Feature;
import net.wurstclient.features.SearchTags;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.Command;

@HelpPage("Commands/xray")
@SearchTags({ "X-Ray", "x ray", "OreFinder", "ore finder" })
public final class XRayCmd extends Command
{
    public XRayCmd() {
        super("xray", "Manages X-Ray's block list.", new String[] { "add <block_name_or_id>", "remove <block_name_or_id>", "list [<page>]", "reset" });
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { XRayCmd.WURST.hax.xRayMod };
    }
    
    @Override
    public void call(final String[] args) throws CmdException {
        if (args.length == 0) {
            throw new CmdSyntaxError();
        }
        final String lowerCase;
        switch (lowerCase = args[0].toLowerCase()) {
            case "remove": {
                this.remove(args);
                return;
            }
            case "add": {
                this.add(args);
                return;
            }
            case "list": {
                this.list(args);
                return;
            }
            case "reset": {
                XRayCmd.WURST.hax.xRayMod.resetBlocks();
                ChatUtils.message("Reset X-Ray's block list to defaults.");
                return;
            }
            default:
                break;
        }
        throw new CmdSyntaxError();
    }
    
    private void add(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        final Block block = Block.getBlockFromName(args[1]);
        if (block == null) {
            throw new CmdSyntaxError("Unknown block: \"" + args[1] + "\".");
        }
        final XRayMod xray = XRayCmd.WURST.hax.xRayMod;
        final String name = WBlock.getName(block);
        if (xray.getIndex(name) > 0) {
            throw new CmdError("X-Ray's block list already contains " + name + ".");
        }
        xray.addBlock(block);
        ChatUtils.message("Added " + name + " to X-Ray.");
        if (xray.isActive()) {
            ChatUtils.message("It will start to show up after you restart X-Ray.");
        }
    }
    
    private void remove(final String[] args) throws CmdException {
        if (args.length != 2) {
            throw new CmdSyntaxError();
        }
        final Block block = Block.getBlockFromName(args[1]);
        if (block == null) {
            throw new CmdSyntaxError("Unknown block: \"" + args[1] + "\".");
        }
        final XRayMod xray = XRayCmd.WURST.hax.xRayMod;
        final String name = WBlock.getName(block);
        final int index = xray.getIndex(name);
        if (index < 0) {
            throw new CmdError("X-Ray's block list does not contain " + name + ".");
        }
        xray.removeBlock(index);
        ChatUtils.message("Removed " + name + " from X-Ray.");
        if (xray.isActive()) {
            ChatUtils.message("It will no longer show up after you restart X-Ray.");
        }
    }
    
    private void list(final String[] args) throws CmdSyntaxError {
        if (args.length > 2) {
            throw new CmdSyntaxError();
        }
        int page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            if (!MathUtils.isInteger(args[1])) {
                throw new CmdSyntaxError("Not a number: " + args[1]);
            }
            page = Integer.parseInt(args[1]);
        }
        final List<String> blocks = XRayCmd.WURST.hax.xRayMod.getBlockNames();
        final int pages = Math.max((int)Math.ceil(blocks.size() / 8.0), 1);
        if (page > pages || page < 1) {
            throw new CmdSyntaxError("Invalid page: " + page);
        }
        ChatUtils.message("Total: " + blocks.size() + ((blocks.size() == 1) ? " block" : " blocks"));
        ChatUtils.message("X-Ray blocks list (page " + page + "/" + pages + ")");
        for (int i = (page - 1) * 8; i < blocks.size() && i < page * 8; ++i) {
            ChatUtils.message(blocks.get(i));
        }
    }
}
