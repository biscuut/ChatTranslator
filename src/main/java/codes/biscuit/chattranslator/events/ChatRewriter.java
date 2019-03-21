package codes.biscuit.chattranslator.events;

import codes.biscuit.chattranslator.ChatTranslator;
import codes.biscuit.chattranslator.utils.ConfigUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Pattern;

public class ChatRewriter {

    private ChatTranslator main;

    public ChatRewriter(ChatTranslator main) {
        this.main = main;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onChatReceive(ClientChatReceivedEvent e) {
        if (!(e.type == 0) && main.getConfigUtils().translateMode != ConfigUtils.TranslateMode.ALLMESSAGES) {
            return;
        }
        if (e.message.getUnformattedText().length() > 2 && !e.message.getUnformattedText().substring(2).startsWith(main.getConfigUtils().symbol+" ")) { // If the message doesn't have an airplane at the start (not processed yet)
            boolean foundCharacter = true;
            if (main.getConfigUtils().translateMode == ConfigUtils.TranslateMode.REGULAR) { // Check for foreign characters
                foundCharacter = false;
                String newMessage = e.message.getUnformattedText();
                if (main.getUtils().isOnHypixel()) {
                    if (!e.message.getUnformattedText().startsWith("From") && !e.message.getUnformattedText().startsWith("To")) { // Don't want to mess up tabs
                        if (e.message.getUnformattedText().contains(":")) {
                            newMessage = e.message.getUnformattedText().split(Pattern.quote(":"), 2)[1];
                        }
                    } else {
                        return;
                    }
                    if (!e.message.getUnformattedText().contains(":")) {
                        return;
                    }
                }
                String exclusions = "\u2764\u221A\u03C0\u270E\u2609\u256F\u25A1\uFF09\uFE35\u253B\u2501\u30C4\u279C\u2615\u2714\u2716\u272E\u2730\u25CF\u2718\u27B2\u25E1\uFF9F"; // Lets hope that last one is the degree thingy
                for (char letter : newMessage.toCharArray()) {
                    if (letter > 191 && !exclusions.contains(String.valueOf(letter))) {
                        foundCharacter = true;
                    }
                }
            }
            if (foundCharacter) { // If a foreign character is found / translateAllMessages is true
                if (main.getConfigUtils().translateMode != ConfigUtils.TranslateMode.ALLMESSAGES) {
                    for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) { // Make sure this message is from a player by checking if the message contains someone's name in the current lobby (some are fake labeled as player messages)
                        if (player.getGameProfile().getName() != null && e.message.getUnformattedText().contains(player.getGameProfile().getName())) {
                            if (!main.getConfigUtils().translateSelf && player.getGameProfile().getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
                                continue;
                            }
                            e.setCanceled(true); // Cancel and process the message
                            main.getUtils().translate(e.message);
                            return;
                        }
                    }
                } else {
                    e.setCanceled(true); // Cancel and process the message
                    main.getUtils().translate(e.message);
                }
            }
        }
    }
}
