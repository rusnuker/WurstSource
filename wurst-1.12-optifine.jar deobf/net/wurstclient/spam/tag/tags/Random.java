// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.spam.tag.tags;

import net.wurstclient.spam.exceptions.SpamException;
import net.minecraft.util.ChatAllowedCharacters;
import net.wurstclient.util.MathUtils;
import net.wurstclient.spam.exceptions.InvalidArgumentException;
import net.wurstclient.spam.exceptions.MissingArgumentException;
import net.wurstclient.spam.tag.TagData;
import net.wurstclient.spam.tag.Tag;

public class Random extends Tag
{
    private static final java.util.Random random;
    
    static {
        random = new java.util.Random();
    }
    
    public Random() {
        super("random", "Generates random strings, numbers and junk.", "<random \"number\"|\"string\"|\"junk\" length>", "Random number: <random number 3>\nRandom string: <random string 5>\nRandom junk: <random junk 8>");
    }
    
    @Override
    public String process(final TagData tagData) throws SpamException {
        if (tagData.getTagArgs().length < 2) {
            throw new MissingArgumentException("The <random> tag requires at least two arguments.", tagData.getTagLine(), this);
        }
        if (!tagData.getTagArgs()[0].equals("number") && !tagData.getTagArgs()[0].equals("string") && !tagData.getTagArgs()[0].equals("junk")) {
            throw new InvalidArgumentException("Invalid type in <random> tag: \"" + tagData.getTagArgs()[0] + "\"", tagData.getTagLine(), this);
        }
        if (!MathUtils.isInteger(tagData.getTagArgs()[1])) {
            throw new InvalidArgumentException("Invalid number in <random> tag: \"" + tagData.getTagArgs()[1] + "\"", tagData.getTagLine(), this);
        }
        String result = "";
        if (tagData.getTagArgs()[0].equals("number")) {
            for (int i = 0; i < Integer.valueOf(tagData.getTagArgs()[1]); ++i) {
                result = String.valueOf(result) + Random.random.nextInt(10);
            }
        }
        else if (tagData.getTagArgs()[0].equals("string")) {
            final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int j = 0; j < Integer.valueOf(tagData.getTagArgs()[1]); ++j) {
                final char nextChar = alphabet.charAt(Random.random.nextInt(alphabet.length()));
                result = String.valueOf(result) + new String(new char[] { nextChar });
            }
        }
        else if (tagData.getTagArgs()[0].equals("junk")) {
            for (int i = 0; i < Integer.valueOf(tagData.getTagArgs()[1]); ++i) {
                final byte[] nextChar2 = { 0 };
                Random.random.nextBytes(nextChar2);
                if (ChatAllowedCharacters.isAllowedCharacter((char)nextChar2[0])) {
                    final String nextString = new String(nextChar2).replace("<", "§_lt;").replace("§", "");
                    result = String.valueOf(result) + nextString;
                }
            }
        }
        return String.valueOf(result) + tagData.getTagContent();
    }
}
