package codes.biscuit.chattranslator.commands;

import codes.biscuit.chattranslator.ChatTranslator;
import codes.biscuit.chattranslator.utils.YandexLanguage;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            List<String> options = Arrays.asList("self", "translateall", "key", "language", "colour", "color");
            List<String> arguments = new ArrayList<>(options);
            for (String arg : options) {
                if (!arg.startsWith(args[0].toLowerCase())) {
                    arguments.remove(arg);
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
                case "translateall":
                    if (main.getConfigUtils().translateAllMessages) {
                        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "You will no longer translate all player messages."));
                    } else {
                        sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "You will now translate all player messages (may waste more character quota!)."));
                    }
                    main.getConfigUtils().translateAllMessages = !main.getConfigUtils().translateAllMessages;
                    main.getConfigUtils().saveConfig();
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
            }
        }
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------" + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + " ChatTranslator " + EnumChatFormatting.GRAY + "]" + EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH + "--------------"));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr self " + EnumChatFormatting.GRAY + "- Toggle whether to translate your own chat."));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr translateall " + EnumChatFormatting.GRAY + "- Toggle whether to translate ALL player chat (may waste precious quota)."));
//       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr [livetranslate|lt] <off|language> " + EnumChatFormatting.GRAY + "- Translate all outgoing chat into another language!"));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr key <key> " + EnumChatFormatting.GRAY + "- Enter your Yandex api key."));
       sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr language <language> " + EnumChatFormatting.GRAY + "- Set the translation result language."));
        sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr colour <colour> " + EnumChatFormatting.GRAY + "- Set the colour of the translation result."));
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "v1.0.2" + " by Biscut - ")
               .appendSibling(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "Powered by Yandex Translate").setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://translate.yandex.com/")))));
       sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "----------------------------------------------"));
    }

    private static void sendMessage(IChatComponent sendMessage) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(sendMessage); // Just for logs
        MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent((byte)1, sendMessage)); // Let other mods pick up the new message
    }
}
