// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.features.mods.chat;

import net.minecraft.client.gui.GuiNewChat;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.wurstclient.util.MathUtils;
import net.wurstclient.Category;
import net.wurstclient.features.Bypasses;
import net.wurstclient.features.SearchTags;
import net.wurstclient.events.ChatInputListener;
import net.wurstclient.features.Hack;

@SearchTags({ "NoSpam", "ChatFilter", "anti spam", "no spam", "chat filter" })
@Bypasses
public final class AntiSpamMod extends Hack implements ChatInputListener
{
    public AntiSpamMod() {
        super("AntiSpam", "Blocks chat spam by adding a counter to repeated messages.");
        this.setCategory(Category.CHAT);
    }
    
    @Override
    public void onEnable() {
        AntiSpamMod.EVENTS.add(ChatInputListener.class, this);
    }
    
    @Override
    public void onDisable() {
        AntiSpamMod.EVENTS.remove(ChatInputListener.class, this);
    }
    
    @Override
    public void onReceivedMessage(final ChatInputEvent event) {
        final List<ChatLine> chatLines = event.getChatLines();
        if (chatLines.isEmpty()) {
            return;
        }
        final GuiNewChat chat = AntiSpamMod.MC.ingameGUI.getChatGUI();
        final int maxTextLength = MathUtils.floor(chat.getChatWidth() / chat.getChatScale());
        final List<ITextComponent> newLines = GuiUtilRenderComponents.splitText(event.getComponent(), maxTextLength, AntiSpamMod.MC.fontRendererObj, false, false);
        int spamCounter = 1;
        int matchingLines = 0;
        for (int i = chatLines.size() - 1; i >= 0; --i) {
            final String oldLine = chatLines.get(i).getChatComponent().getUnformattedText();
            if (matchingLines <= newLines.size() - 1) {
                final String newLine = newLines.get(matchingLines).getUnformattedText();
                if (matchingLines < newLines.size() - 1) {
                    if (oldLine.equals(newLine)) {
                        ++matchingLines;
                        continue;
                    }
                    matchingLines = 0;
                    continue;
                }
                else {
                    if (!oldLine.startsWith(newLine)) {
                        matchingLines = 0;
                        continue;
                    }
                    if (i > 0 && matchingLines == newLines.size() - 1) {
                        final String twoLines = String.valueOf(oldLine) + chatLines.get(i - 1).getChatComponent().getUnformattedText();
                        final String addedText = twoLines.substring(newLine.length());
                        if (addedText.startsWith(" [x") && addedText.endsWith("]")) {
                            final String oldSpamCounter = addedText.substring(3, addedText.length() - 1);
                            if (MathUtils.isInteger(oldSpamCounter)) {
                                spamCounter += Integer.parseInt(oldSpamCounter);
                                ++matchingLines;
                                continue;
                            }
                        }
                    }
                    if (oldLine.length() == newLine.length()) {
                        ++spamCounter;
                    }
                    else {
                        final String addedText2 = oldLine.substring(newLine.length());
                        if (!addedText2.startsWith(" [x") || !addedText2.endsWith("]")) {
                            matchingLines = 0;
                            continue;
                        }
                        final String oldSpamCounter2 = addedText2.substring(3, addedText2.length() - 1);
                        if (!MathUtils.isInteger(oldSpamCounter2)) {
                            matchingLines = 0;
                            continue;
                        }
                        spamCounter += Integer.parseInt(oldSpamCounter2);
                    }
                }
            }
            for (int i2 = i + matchingLines; i2 >= i; --i2) {
                chatLines.remove(i2);
            }
            matchingLines = 0;
        }
        if (spamCounter > 1) {
            event.getComponent().appendText(" [x" + spamCounter + "]");
        }
    }
}
