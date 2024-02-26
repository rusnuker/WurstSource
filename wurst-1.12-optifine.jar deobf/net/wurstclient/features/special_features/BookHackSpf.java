// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.special_features;

import net.wurstclient.features.Feature;
import net.wurstclient.features.HelpPage;
import net.wurstclient.features.SearchTags;
import net.wurstclient.DontBlock;
import net.wurstclient.features.OtherFeature;

@DontBlock
@SearchTags({ "Force OP", "Book Hack", "OP Book", "command book" })
@HelpPage("Special_Features/Force_OP_(BookHack)")
public final class BookHackSpf extends OtherFeature
{
    public BookHackSpf() {
        super("BookHack", "Allows you to insert links that execute commands into writable books. This can be used to\ntrick other people (including admins) into executing commands like \"/op YourName\" or\n\"/kill\".");
    }
    
    @Override
    public Feature[] getSeeAlso() {
        return new Feature[] { BookHackSpf.WURST.hax.forceOpMod };
    }
}
