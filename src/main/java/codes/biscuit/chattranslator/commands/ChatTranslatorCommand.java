package codes.biscuit.chattranslator.commands;

import codes.biscuit.chattranslator.ChatTranslator;
import codes.biscuit.chattranslator.utils.ConfigUtils;
import codes.biscuit.chattranslator.utils.Utils;
import codes.biscuit.chattranslator.utils.YandexLanguage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
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
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your key has been updated!"));
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a key: /ctr key <key>"));
                    }
                    return;
                case "rules": case "rule":
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("list")) {
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Here are the rules you can change:"));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF 0 - " + main.getConfigUtils().getRuleString(ConfigUtils.Rule.MUSTCONTAINCHARACTER) + " - " + EnumChatFormatting.GRAY + "The message must contain a foreign character."));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF 1 - " + main.getConfigUtils().getRuleString(ConfigUtils.Rule.MESSAGEMUSTBESENTASPLAYER) + " - " + EnumChatFormatting.GRAY + "The message must be reported by the server as a player message (inconsistent)."));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF 2 - " + main.getConfigUtils().getRuleString(ConfigUtils.Rule.PLAYERMUSTBEINSERVER) + " - " + EnumChatFormatting.GRAY + "The message must contain the name of a player in your current server (to avoid server messages)."));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF 3 - " + main.getConfigUtils().getRuleString(ConfigUtils.Rule.MUSTBEHYPIXELSTYLE) + " - " + EnumChatFormatting.GRAY + "The message must be in Hypixel chat format (more consistent)."));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF 4 - " + main.getConfigUtils().getRuleString(ConfigUtils.Rule.MUSTNOTBESENTBYSELF) + " - " + EnumChatFormatting.GRAY + "The message must not be sent by yourself."));
                        } else if (args[1].equalsIgnoreCase("set")) {
                            if (args.length > 2) {
                                int ruleID;
                                try {
                                    ruleID = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ex) {
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "The rule must be a number!"));
                                    return;
                                }
                                ConfigUtils.Rule rule = ConfigUtils.Rule.fromId(ruleID);
                                if (rule != null) {
                                    if (args.length > 3) {
                                        boolean setting;
                                        try {
                                            setting = Boolean.parseBoolean(args[3]);
                                        } catch (NumberFormatException ex) {
                                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "You must specify true/false!"));
                                            return;
                                        }
                                        main.getConfigUtils().getRules().put(rule, setting);
                                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully changed rule id "+ruleID+" to "+setting+"!"));
                                        main.getConfigUtils().saveConfig();
                                    }
                                } else {
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid rule! (0-3)"));
                                }
                            } else {
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify the rule id: /ctr rules set <ruleid> <true|false>"));
                            }
                        } else {
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid argument! /ctr rules <list|set> [ruleid] [true|false]"));
                        }
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify an argument: /ctr rules <list|set> [ruleid]  [true|false]"));
                    }
                    return;
                case "language":
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("list")) {
                            StringBuilder languages = new StringBuilder();
                            String[] colors = {"a","b","c","d","e","f","1","2","3","4","5","6","7","8","9"};
                            int colorIndex = 0;
                            Set<String> languageSet = new TreeSet<>();
                            for (YandexLanguage language : YandexLanguage.values()) {
                                languageSet.add(language.toString());
                            }
                            for (String language : languageSet) {
                                if (colorIndex>colors.length-1) colorIndex=0;
                                languages.append("\u00a7").append(colors[colorIndex++]).append(language.toLowerCase()).append(" ");
                            }
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Here are a list of supported languages:"));
                            Utils.sendMessage(new ChatComponentText(languages.toString()));
                        } else {
                            try {
                                main.getConfigUtils().setLang(YandexLanguage.valueOf(args[1].toUpperCase()).getLanguageCode());
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your language has been set to: " + args[1] + "."));
                                main.getConfigUtils().saveConfig();
                            } catch (Exception ex) {
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "This language is invalid, please try another or check your spelling."));
                            }
                        }
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a language or list: /ctr language <language|list>"));
                    }
                    return;
                case "colour": case "color":
                    if (args.length > 1) {
                        String argColor = args[1];
                        if (args.length > 2) argColor += "_"+args[2];
                        if (argColor.equalsIgnoreCase("list")) {
                            StringBuilder languages = new StringBuilder();
                            for (EnumChatFormatting color : EnumChatFormatting.values()) {
                                languages.append(color.toString()).append(color.name().toLowerCase().replace("_", " ")).append(EnumChatFormatting.RESET).append(" ");
                            }
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Here are a list of supported colors:"));
                            Utils.sendMessage(new ChatComponentText(languages.toString()));
                        } else {
                            try {
                                main.getConfigUtils().setTranslationColour(EnumChatFormatting.valueOf(argColor.toUpperCase()));
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your translation color has been set to: " + argColor + "."));
                                main.getConfigUtils().saveConfig();
                            } catch (Exception ex) {
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "This color is invalid, please try another or check your spelling."));
                            }
                        }
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a colour or list: /ctr colour <color|list>"));
                    }
                    return;
                case "symbol":
                    if (args.length > 1) {
                        if (args[1].length() == 1) {
                            main.getConfigUtils().setSymbol(args[1].charAt(0));
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your symbol has been set to: " + args[1].charAt(0) + "."));
                            main.getConfigUtils().saveConfig();
                        } else {
                            Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "The symbol must be a single character!"));
                        }
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify a symbol: /ctr symbol <symbol>"));
                    }
                    return;
                case "blacklist":
                    if (args.length > 1) {
                        switch (args[1].toLowerCase()) {
                            case "list":
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Here are a list of currently blacklisted prefixes:"));
                                for (String entry : main.getConfigUtils().getPrefixBlacklist()) {
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF "+EnumChatFormatting.WHITE+entry));
                                }
                                break;
                            case "add":
                                if (args.length > 2) {
                                    main.getConfigUtils().getPrefixBlacklist().add(args[2]);
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Added "+args[2]+" to the blacklist!"));
                                } else {
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify an entry: /ctr blacklist add <entry>"));
                                }
                                main.getConfigUtils().saveConfig();
                                break;
                            case "remove":
                                if (args.length > 2) {
                                    main.getConfigUtils().getPrefixBlacklist().remove(args[2]);
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Removed "+args[2]+" from the blacklist!"));
                                } else {
                                    Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify an entry: /ctr blacklist remove <entry>"));
                                }
                                main.getConfigUtils().saveConfig();
                                break;
                            default:
                                Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid argument: /ctr blacklist <list|add|remove> [entry]"));
                        }
                    } else {
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "Please specify an argument: /ctr blacklist <list|add|remove> [entry]"));
                    }
                    return;
                case "copyclipboard":
                    if (args.length > 1) {
                        StringBuilder text = new StringBuilder();
                        int counter = 0;
                        for (String arg : args) { // make a string by combining all the arguments
                            if (counter == 0) {
                                counter++;
                                continue;
                            }
                            text.append(arg);
                            if (counter != args.length-1) {
                                text.append(" ");
                            }
                            counter++;
                        }
                        StringSelection stringSelection = new StringSelection(text.toString());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Text copied to clipboard!"));
                    }
                    return;
            }
        }
       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "--------------" + EnumChatFormatting.GRAY + "[" + EnumChatFormatting.RED + EnumChatFormatting.BOLD + " ChatTranslator " + EnumChatFormatting.GRAY + "]" + EnumChatFormatting.GRAY + EnumChatFormatting.STRIKETHROUGH + "--------------"));
//       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr self " + EnumChatFormatting.GRAY + "- Toggle whether to translate your own chat."));
        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr rules <list|set> [ruleid] [true|false]" + EnumChatFormatting.GRAY + "- Customize when text should be translated with specific rules."));
//       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr [livetranslate|lt] <off|language> " + EnumChatFormatting.GRAY + "- Translate all outgoing chat into another language!"));
       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr key <key> " + EnumChatFormatting.GRAY + "- Enter your Yandex api key."));
       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr language <language|list> " + EnumChatFormatting.GRAY + "- Set the translation result language."));
        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr color <color|list> " + EnumChatFormatting.GRAY + "- Set the colour of the translation result."));
        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr blacklist <list|add|remove> [entry] " + EnumChatFormatting.GRAY + "- Add/remove chat blacklist prefixes (if a message starts with anything added here it won't translate)."));
        Utils.sendMessage(new ChatComponentText(EnumChatFormatting.RED + "\u25CF /ctr symbol <symbol> " + EnumChatFormatting.GRAY + "- Change the prefix symbol from an airplane to something else."));
       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "v1.0.6" + " by Biscut - ")
               .appendSibling(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.ITALIC + "Powered by Yandex Translate").setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://translate.yandex.com/")))));
       Utils.sendMessage(new ChatComponentText(EnumChatFormatting.GRAY.toString() + EnumChatFormatting.STRIKETHROUGH + "----------------------------------------------"));
    }
}
