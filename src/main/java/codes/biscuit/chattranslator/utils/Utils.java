package codes.biscuit.chattranslator.utils;

import codes.biscuit.chattranslator.ChatTranslator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


public class Utils {

    private ChatTranslator main;

    public Utils(ChatTranslator main) {
        this.main = main;
    }

    public void translate(final IChatComponent originalMessage) {
        String newMessage = originalMessage.getUnformattedText();
        if (originalMessage.getUnformattedText().contains(":") && isOnHypixel()) {  // Only translate after the semicolon if on Hypixel
            newMessage = originalMessage.getUnformattedText().split(Pattern.quote(": "), 2)[1];
        }
        final String finalMessage = newMessage;
        new Thread(new Runnable() { // New thread for the HTTP request
            public void run() {
                HttpsURLConnection conn = null;
                try {
                    URL url = new URL(("https://translate.yandex.net/api/v1.5/tr.json/translate" +
                            "?key=" + URLEncoder.encode(main.getConfigUtils().getKey(), "UTF-8")
                            + "&lang=" + URLEncoder.encode(main.getConfigUtils().getLang(), "UTF-8")
                            + "&text=" + URLEncoder.encode(finalMessage, "UTF-8")));
                    conn = (HttpsURLConnection)url.openConnection();
                    conn.setRequestProperty("Content-Type", "text/plain; charset=" + "UTF-8");
                    conn.setRequestProperty("Accept-Charset", "UTF-8");
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    StringBuilder outputBuilder = new StringBuilder();
                    String nextLine;
                    if (conn.getInputStream() != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                        while (null != (nextLine = reader.readLine())) {
                            outputBuilder.append(nextLine);
                        }
                    }
                    String result = outputBuilder.toString();
                    if (responseCode != 200) {
                        throw new Exception();
                    }
                    String properResult = new Gson().fromJson(result, JsonObject.class).get("text").toString();
                    String languageCode = new Gson().fromJson(result, JsonObject.class).get("lang").toString().split(Pattern.quote("-"))[0].substring(1);
                    YandexLanguage detectedLanguage = YandexLanguage.fromLanguageCode(languageCode);
                    properResult = properResult.trim().substring(0, properResult.length() - 2).substring(2);
                    if (detectedLanguage == YandexLanguage.fromLanguageCode(main.getConfigUtils().getLang())) {
                        IChatComponent message = originalMessage.setChatStyle(originalMessage.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ctr copyclipboard "+finalMessage)));
                        addPrefixAndSend(message, 2);
                    } else {
                        IChatComponent message = originalMessage.setChatStyle(originalMessage.getChatStyle().setChatHoverEvent( // Add the translation as hover text and send the message
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(main.getConfigUtils().getTranslationColour() + "(" + detectedLanguage + ") " + properResult))));
                        message.setChatStyle(message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ctr copyclipboard "+finalMessage)));
                        addPrefixAndSend(message, 0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    IChatComponent message = originalMessage.setChatStyle(originalMessage.getChatStyle().setChatHoverEvent( // Add the translation as hover text and send the message
                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.RED + "Something went wrong- you may have run out of quota or misspelled your key. Click to view/make a new key."))));
                    message.setChatStyle(message.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://translate.yandex.com/developers/keys")));
                    addPrefixAndSend(message, 1);
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    private void addPrefixAndSend(IChatComponent message, int prefixType) {
        if (prefixType == 0) { // Translation success
            message = new ChatComponentText(EnumChatFormatting.GREEN.toString() + main.getConfigUtils().getSymbol() +" ").appendSibling(message);
        } else if (prefixType == 1) { // Translation failure
            message = new ChatComponentText(EnumChatFormatting.RED.toString() + main.getConfigUtils().getSymbol() +" ").appendSibling(message);
        } else { // Translation
            message = new ChatComponentText(EnumChatFormatting.GRAY.toString() + main.getConfigUtils().getSymbol() +" ").appendSibling(message);
        }
        sendMessage(message);
    }

    public static void sendMessage(IChatComponent sendMessage) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent((byte)1, sendMessage);
        MinecraftForge.EVENT_BUS.post(event); // Let other mods pick up the new message
        if (!event.isCanceled()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(event.message); // Just for logs
        }
    }

    public boolean isOnHypixel() {
        if (Minecraft.getMinecraft().getCurrentServerData() != null) {
            String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase();
            return (ip.equals("hypixel.net") || ip.endsWith(".hypixel.net") || ip.contains(".hypixel.net:") || ip.startsWith("hypixel.net:"));
        } else {
            return false;
        }
    }
}