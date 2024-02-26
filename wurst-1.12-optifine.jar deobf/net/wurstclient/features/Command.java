// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features;

import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.keybinds.PossibleKeybind;
import java.util.ArrayList;

public abstract class Command extends Feature
{
    private final String name;
    private final String description;
    private final String[] syntax;
    
    public Command(final String name, final String description, final String... syntax) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
    }
    
    public final String getCmdName() {
        return this.name;
    }
    
    public final String[] getSyntax() {
        return this.syntax;
    }
    
    @Override
    public final String getName() {
        return "." + this.name;
    }
    
    @Override
    public final String getType() {
        return "Command";
    }
    
    @Override
    public final String getDescription() {
        String description = this.description;
        if (this.syntax.length > 0) {
            description = String.valueOf(description) + "\n\nSyntax:";
        }
        String[] syntax;
        for (int length = (syntax = this.syntax).length, i = 0; i < length; ++i) {
            final String element = syntax[i];
            description = String.valueOf(description) + "\n  ." + this.name + " " + element;
        }
        return description;
    }
    
    @Override
    public final boolean isEnabled() {
        return false;
    }
    
    @Override
    public final boolean isBlocked() {
        return false;
    }
    
    @Override
    public final ArrayList<PossibleKeybind> getPossibleKeybinds() {
        return new ArrayList<PossibleKeybind>();
    }
    
    @Override
    public String getPrimaryAction() {
        return "";
    }
    
    @Override
    public void doPrimaryAction() {
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[0];
    }
    
    public final void printHelp() {
        String[] split;
        for (int length = (split = this.description.split("\n")).length, i = 0; i < length; ++i) {
            final String line = split[i];
            ChatUtils.message(line);
        }
    }
    
    public final void printSyntax() {
        String output = "§o." + this.name + "§r";
        if (this.syntax.length != 0) {
            output = String.valueOf(output) + " " + this.syntax[0];
            for (int i = 1; i < this.syntax.length; ++i) {
                output = String.valueOf(output) + "\n    " + this.syntax[i];
            }
        }
        String[] split;
        for (int length = (split = output.split("\n")).length, j = 0; j < length; ++j) {
            final String line = split[j];
            ChatUtils.message(line);
        }
    }
    
    protected final BlockPos argsToPos(final String... args) throws CmdException {
        if (args.length == 3) {
            final BlockPos playerPos = new BlockPos(Command.MC.player);
            final int[] player = { playerPos.getX(), playerPos.getY(), playerPos.getZ() };
            final int[] pos = new int[3];
            for (int i = 0; i < 3; ++i) {
                if (MathUtils.isInteger(args[i])) {
                    pos[i] = Integer.parseInt(args[i]);
                }
                else if (args[i].equals("~")) {
                    pos[i] = player[i];
                }
                else {
                    if (!args[i].startsWith("~") || !MathUtils.isInteger(args[i].substring(1))) {
                        throw new CmdSyntaxError("Invalid coordinates.");
                    }
                    pos[i] = player[i] + Integer.parseInt(args[i].substring(1));
                }
            }
            return new BlockPos(pos[0], pos[1], pos[2]);
        }
        if (args.length != 1) {
            throw new CmdSyntaxError("Invalid coordinates.");
        }
        final EntityUtils.TargetSettings settings = new EntityUtils.TargetSettings() {
            @Override
            public boolean targetFriends() {
                return true;
            }
            
            @Override
            public boolean targetBehindWalls() {
                return true;
            }
        };
        final Entity entity = EntityUtils.getClosestEntityWithName(args[0], settings);
        if (entity == null) {
            throw new CmdError("Entity \"" + args[0] + "\" could not be found.");
        }
        return new BlockPos(entity);
    }
    
    public abstract void call(final String[] p0) throws CmdException;
    
    public abstract class CmdException extends Exception
    {
        public CmdException() {
        }
        
        public CmdException(final String message) {
            super(message);
        }
        
        public abstract void printToChat();
    }
    
    public final class CmdError extends CmdException
    {
        public CmdError(final String message) {
            super(message);
        }
        
        @Override
        public void printToChat() {
            ChatUtils.error(this.getMessage());
        }
    }
    
    public final class CmdSyntaxError extends CmdException
    {
        public CmdSyntaxError() {
        }
        
        public CmdSyntaxError(final String message) {
            super(message);
        }
        
        @Override
        public void printToChat() {
            if (this.getMessage() != null) {
                ChatUtils.message("§4Syntax error:§r " + this.getMessage());
            }
            Command.this.printSyntax();
        }
    }
}
