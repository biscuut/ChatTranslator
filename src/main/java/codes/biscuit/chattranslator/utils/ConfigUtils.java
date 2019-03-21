package codes.biscuit.chattranslator.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;

public class ConfigUtils {

    private File configFile;
    private JsonObject loadedConfig = new JsonObject();

    public String lang = "en";
    String key = "";
    public char symbol = '\u2708';
    public boolean translateSelf = true;
    public TranslateMode translateMode = TranslateMode.REGULAR;
    public EnumChatFormatting translationColour = EnumChatFormatting.YELLOW;

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
                key = loadedConfig.get("key").getAsString();
                symbol = loadedConfig.get("symbol").getAsCharacter();
                lang = loadedConfig.get("lang").getAsString();
                translateSelf = loadedConfig.get("translate-self").getAsBoolean();
                translateMode = TranslateMode.fromID(loadedConfig.get("mode").getAsByte());
                translationColour = EnumChatFormatting.valueOf(loadedConfig.get("translation-colour").getAsString());
            } catch (Exception ex) {
                saveConfig();
            }
        } else {
            saveConfig();
        }
    }

    public void saveConfig() {
        loadedConfig = new JsonObject();
        try {
            if (!configFile.createNewFile()) {
                return;
            }
            FileWriter writer = new FileWriter(configFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            loadedConfig.addProperty("lang", lang);
            loadedConfig.addProperty("key", key);
            loadedConfig.addProperty("symbol", symbol);
            loadedConfig.addProperty("translate-self", translateSelf);
            loadedConfig.addProperty("mode", translateMode.getId());
            loadedConfig.addProperty("translation-colour", translationColour.toString());
            bufferedWriter.write(loadedConfig.toString());
            bufferedWriter.close();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error while attempting to create the config...");
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public enum TranslateMode {
        REGULAR(0),
        ALLPLAYER(1),
        ALLMESSAGES(2);

        private byte id;

        TranslateMode(int id) {
            this.id = (byte)id;
        }

        public int getId() {
            return id;
        }

        public static TranslateMode fromID(int id) {
            for (TranslateMode mode : values()) {
                if (mode.id == (byte)id) {
                    return mode;
                }
            }
            return null;
        }
    }
}
