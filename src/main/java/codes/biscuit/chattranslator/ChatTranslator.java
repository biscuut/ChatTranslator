package codes.biscuit.chattranslator;

import codes.biscuit.chattranslator.commands.ChatTranslatorCommand;
import codes.biscuit.chattranslator.events.ChatRewriter;
import codes.biscuit.chattranslator.utils.ConfigUtils;
import codes.biscuit.chattranslator.utils.Utils;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ChatTranslator.MOD_ID, version = ChatTranslator.VERSION, name = ChatTranslator.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class ChatTranslator {

    static final String MOD_ID = "chattranslator";
    static final String MOD_NAME = "ChatTranslator";
    static final String VERSION = "1.0.4";

    private ConfigUtils configUtils;
    private Utils utils = new Utils(this);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.configUtils = new ConfigUtils(e.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ChatRewriter(this));
        ClientCommandHandler.instance.registerCommand(new ChatTranslatorCommand(this));
        configUtils.loadConfig();
    }

    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    public Utils getUtils() {
        return utils;
    }
}