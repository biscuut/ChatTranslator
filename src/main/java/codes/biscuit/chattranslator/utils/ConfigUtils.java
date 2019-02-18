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
    public boolean translateSelf = true;
    public boolean translateAllMessages = false;
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
                lang = loadedConfig.get("lang").getAsString();
                translateSelf = loadedConfig.get("translate-self").getAsBoolean();
                translateAllMessages = loadedConfig.get("translate-all").getAsBoolean();
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
            loadedConfig.addProperty("translate-self", translateSelf);
            loadedConfig.addProperty("translate-all", translateAllMessages);
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
}
