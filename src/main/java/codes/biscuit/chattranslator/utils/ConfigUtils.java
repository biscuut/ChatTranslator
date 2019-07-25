package codes.biscuit.chattranslator.utils;

import com.google.gson.*;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {

    private File configFile;
    private JsonObject loadedConfig = new JsonObject();

    private String lang = "en";
    private String key = "";
    private char symbol = '\u2708';
    private EnumChatFormatting translationColour = EnumChatFormatting.YELLOW;
    private Set<String> prefixBlacklist = new HashSet<>();
    private Map<Rule, Boolean> rules = new EnumMap<>(Rule.class);

    public ConfigUtils(File configFile) {
        this.configFile = configFile;
    }

    public void loadConfig() {
        if (configFile.exists()) {
            try {
                FileReader reader = new FileReader(configFile);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String nextLine;
                while ((nextLine = bufferedReader.readLine()) != null) {
                    builder.append(nextLine);
                }
                String complete = builder.toString();
                loadedConfig = new JsonParser().parse(complete).getAsJsonObject();
                setKey(loadedConfig.get("key").getAsString());
                String characterString;
                if (loadedConfig.has("symbol")) {
                    characterString = loadedConfig.get("symbol").getAsString();
                } else {
                    characterString = String.valueOf((int)symbol);
                }
                int character;
                try {
                    character = Integer.parseInt(characterString);
                } catch (NumberFormatException ex) {
                    character = (int)symbol;
                }
                setSymbol((char)character);
                setLang(loadedConfig.get("lang").getAsString());
                String color = loadedConfig.get("translation-colour").getAsString();
                int colorId; // backwards compat
                try {
                    colorId = Integer.parseInt(color);
                } catch (NumberFormatException ex) {
                    colorId = translationColour.getColorIndex();
                }
                setTranslationColour(EnumChatFormatting.func_175744_a(colorId));
                addMapDefaults(true);
                saveConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
                saveConfig();
            }
        } else {
            addMapDefaults(false);
            saveConfig();
        }
    }

    private void addMapDefaults(boolean loadIfNot) {
        if (!loadedConfig.has("rule0")) {
            rules.put(Rule.MUSTCONTAINCHARACTER, true);
            rules.put(Rule.MUSTBEHYPIXELSTYLE, true);
            rules.put(Rule.PLAYERMUSTBEINSERVER, false);
            rules.put(Rule.MESSAGEMUSTBESENTASPLAYER, false);
            rules.put(Rule.MUSTNOTBESENTBYSELF, true);
        } else {
            if (loadIfNot) {
                for (Rule rule : Rule.values()) {
                    rules.put(rule, loadedConfig.get("rule" + rule.getId()).getAsBoolean());
                }
            }
        }
        if (!loadedConfig.has("blacklist")) {
            prefixBlacklist.add("[T]");
            prefixBlacklist.add("To ");
            prefixBlacklist.add("From ");
        } else {
            if (loadIfNot) {
                Set<String> blacklist = new HashSet<>();
                for (JsonElement element : loadedConfig.getAsJsonArray("blacklist")) {
                    blacklist.add(element.getAsString());
                }
                prefixBlacklist = blacklist;
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveConfig() {
        loadedConfig = new JsonObject();
        try {
            configFile.createNewFile();
            FileWriter writer = new FileWriter(configFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            loadedConfig.addProperty("lang", getLang());
            loadedConfig.addProperty("key", getKey());
            loadedConfig.addProperty("symbol", (int)getSymbol());
            loadedConfig.addProperty("translation-colour", getTranslationColour().getColorIndex());
            JsonArray jsonArray = new JsonArray();
            for (String element : prefixBlacklist) {
                jsonArray.add(new GsonBuilder().create().toJsonTree(element));
            }
            loadedConfig.add("blacklist", jsonArray);
            for (Rule rule : Rule.values()) {
                if (rules.containsKey(rule)) {
                    loadedConfig.addProperty("rule" + rule.getId(), rules.get(rule));
                }
            }
            bufferedWriter.write(loadedConfig.toString());
            bufferedWriter.close();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error while attempting to create the config...");
        }
    }

    public String getRuleString(Rule rule) {
        if (rules.containsKey(rule)) {
            if (rules.get(rule)) {
                return EnumChatFormatting.GREEN+"true";
            } else {
                return EnumChatFormatting.RED+"false";
            }
        }
        return "";
    }

    public enum Rule {
        MUSTCONTAINCHARACTER(0),
        MESSAGEMUSTBESENTASPLAYER(1),
        PLAYERMUSTBEINSERVER(2),
        MUSTBEHYPIXELSTYLE(3),
        MUSTNOTBESENTBYSELF(4);

        private int id;

        Rule(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static Rule fromId(int id) {
            for (Rule rule : values()) {
                if (rule.id == id) {
                    return rule;
                }
            }
            return null;
        }
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Map<Rule, Boolean> getRules() {
        return rules;
    }

    public Set<String> getPrefixBlacklist() {
        return prefixBlacklist;
    }

    String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    String getKey() {
        return key;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    EnumChatFormatting getTranslationColour() {
        return translationColour;
    }

    public void setTranslationColour(EnumChatFormatting translationColour) {
        this.translationColour = translationColour;
    }
}
