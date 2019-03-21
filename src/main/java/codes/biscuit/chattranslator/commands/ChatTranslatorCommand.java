package codes.biscuit.chattranslator.commands;

import codes.biscuit.chattranslator.ChatTranslator;
import codes.biscuit.chattranslator.utils.ConfigUtils;
import codes.biscuit.chattranslator.utils.YandexLanguage;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;

public class ChatTranslatorCommand extends CommandBase {

    private ChatTranslator main;

    public ChatTranslatorCommand(ChatTranslator main) {
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "chattranslator";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Collections.singletonList("ctr");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>(Arrays.asList("self", "mode", "key", "language", "colour", "symbol"));
            Iterator<String> argumentIterator = arguments.listIterator();
            while (argumentIterator.hasNext()) {
                if (!argumentIterator.next().startsWith(args[0].toLowerCase())) {
                    argumentIterator.remove();
                }
            }
            return arguments;
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            switch (args[0]) {
                case "key":
                    if (args.length > 1) {
                        main.getConfigUtils().setKey(args[1]);
                        main.getConfigUtils().saveConfig();
                        sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your key has been updated!"));
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a key: /ctr key <key>"));
                    }
                    return;
                case "self":
                    if (main.getConfigUtils().translateSelf) {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "You will no longer translate your own messages."));
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will now translate your own messages."));
                    }
                    main.getConfigUtils().translateSelf = !main.getConfigUtils().translateSelf;
                    main.getConfigUtils().saveConfig();
                    return;
                case "mode":
                    if (args.length > 1) {
                        ConfigUtils.TranslateMode translateMode;
                        try {
                            translateMode = ConfigUtils.TranslateMode.valueOf(args[1].toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            sendMessage(new ChatComponentText(EnumChatFormatting.RED + "This is not a valid mode! [regular|allplayer|allmessages]"));
                            return;
                        }
                        switch (translateMode) {
                            case REGULAR:
                                sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will now translate player messages with a detected symbol only."));
                                break;
                            case ALLPLAYER:
                                sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will now translate all messages that are sent by a player"));
                                break;
                            case ALLMESSAGES:
                                sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will now translate all messages (except private)."));
                                break;
                        }
                        main.getConfigUtils().translateMode = translateMode;
                        main.getConfigUtils().saveConfig();

                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a mode: /ctr mode [regular|allplayer|allmessages]"));
                    }
                    return;
                case "language":
                    if (args.length > 1) {
                        try {
                            main.getConfigUtils().lang = YandexLanguage.valueOf(args[1].toUpperCase()).getLanguageCode();
                            sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your language has been set to: " + args[1] + "."));
                            main.getConfigUtils().saveConfig();
                        } catch (Exception ex) {
                            sendMessage(new ChatComponentText(EnumChatFormatting.RED + "This language is invalid, please try another or check your spelling."));
                        }
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a language: /ctr language <language>"));
                    }
                    return;
                case "colour": case "color":
                    if (args.length > 1) {
                        try {
                            main.getConfigUtils().translationColour = EnumChatFormatting.valueOf(args[1].toUpperCase());
                            sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your translation colour has been set to: " + args[1] + "."));
                            main.getConfigUtils().saveConfig();
                        } catch (Exception ex) {
                            sendMessage(new ChatComponentText(EnumChatFormatting.RED + "This colour is invalid, please try another or check your spelling."));
                        }
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a colour: /ctr colour <colour>"));
                    }
                    return;
                case "symbol":
                    if (args.length > 1) {
                        if (args[1].length() == 1) {
                            main.getConfigUtils().symbol = args[1].charAt(0);
                            sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your symbol has been set to: " + args[1].charAt(0) + "."));
                            main.getConfigUtils().saveConfig();
                        } else {
                            sendMessage(new ChatComponentText(EnumChatFormatting.RED + "The symbol must be a single character!"));
                        }
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a symbol: /ctr symbol <symbol>"));
                    }
                    return;
            }
        }
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------" + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + " ChatTranslator " + EnumChatFormatting.GRAY + "]" + EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH + "--------------"));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr self " + EnumChatFormatting.GRAY + "- Toggle whether to translate your own chat."));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr mode [regular|allplayer|allmessages] " + EnumChatFormatting.GRAY + "- Toggle the translate mode- other ones may waste precious character quota."));
//       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr [livetranslate|lt] <off|language> " + EnumChatFormatting.GRAY + "- Translate all outgoing chat into another language!"));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr key <key> " + EnumChatFormatting.GRAY + "- Enter your Yandex api key."));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr language <language> " + EnumChatFormatting.GRAY + "- Set the translation result language."));
        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr colour <colour> " + EnumChatFormatting.GRAY + "- Set the colour of the translation result."));
        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr symbol <symbol> " + EnumChatFormatting.GRAY + "- Change the prefix symbol from an airplane to something else."));
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "v1.0.4" + " by Biscut - ")
               .appendSibling(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "Powered by Yandex Translate").setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://translate.yandex.com/")))));
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "----------------------------------------------"));
    }

    private static void sendMessage(IChatComponent sendMessage) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(sendMessage); // Just for logs
        MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent((byte)1, sendMessage)); // Let other mods pick up the new message
    }
}
