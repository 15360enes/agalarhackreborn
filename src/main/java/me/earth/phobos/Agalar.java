package me.earth.phobos;

import me.earth.phobos.features.gui.custom.GuiCustomMainScreen;
import me.earth.phobos.features.modules.client.PhobosChat;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.manager.*;
/*import me.earth.phobos.event.HWIDUtil;
import me.earth.phobos.event.NoStackTraceThrowable;
import me.earth.phobos.event.FrameUtil;
import me.earth.phobos.event.NetworkUtil;*/
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "agalar", name = "AgalarClient", version = "3.2.0")
public
class Agalar {
    public static final String MODID = "AgalarClient";
    public static final String MODNAME = "AgalarClient";
    public static final String MODVER = "3.2.0";
    public static final String NAME_UNICODE = " ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String PHOBOS_UNICODE = " ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String CHAT_SUFFIX = " ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String PHOBOS_SUFFIX = " ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final Logger LOGGER = LogManager.getLogger ( "Agalar" );
    public static ModuleManager moduleManager;
    public static SpeedManager speedManager;
    public static PositionManager positionManager;
    public static RotationManager rotationManager;
    public static CommandManager commandManager;
    public static EventManager eventManager;
    public static ConfigManager configManager;
    public static FileManager fileManager;
    public static FriendManager friendManager;
    public static TextManager textManager;
    public static ColorManager colorManager;
    public static ServerManager serverManager;
    public static PotionManager potionManager;
    public static InventoryManager inventoryManager;
    public static TimerManager timerManager;
    public static PacketManager packetManager;
    public static ReloadManager reloadManager;
    public static TotemPopManager totemPopManager;
    public static HoleManager holeManager;
    public static NotificationManager notificationManager;
    public static SafetyManager safetyManager;
    public static GuiCustomMainScreen customMainScreen;
    public static CosmeticsManager cosmeticsManager;
    public static NoStopManager baritoneManager;
    public static WaypointManager waypointManager;
    @Mod.Instance
    public static Agalar INSTANCE;
    private static boolean unloaded;
    public static List<String> hwidList = new ArrayList<>();

    static {
        unloaded = false;
    }
    public Minecraft mc = Minecraft.getMinecraft();
    public static
    void load ( ) {
        LOGGER.info ( "\n\nLoading AgalarClient 3.2.0" );
        unloaded = false;
        if ( reloadManager != null ) {
            reloadManager.unload ( );
            reloadManager = null;
        }
        baritoneManager = new NoStopManager ( );
        totemPopManager = new TotemPopManager ( );
        timerManager = new TimerManager ( );
        packetManager = new PacketManager ( );
        serverManager = new ServerManager ( );
        colorManager = new ColorManager ( );
        textManager = new TextManager ( );
        moduleManager = new ModuleManager ( );
        speedManager = new SpeedManager ( );
        rotationManager = new RotationManager ( );
        positionManager = new PositionManager ( );
        commandManager = new CommandManager ( );
        eventManager = new EventManager ( );
        configManager = new ConfigManager ( );
        fileManager = new FileManager ( );
        friendManager = new FriendManager ( );
        potionManager = new PotionManager ( );
        inventoryManager = new InventoryManager ( );
        holeManager = new HoleManager ( );
        notificationManager = new NotificationManager ( );
        safetyManager = new SafetyManager ( );
        waypointManager = new WaypointManager ( );
        LOGGER.info ( "Initialized Management" );
        moduleManager.init ( );
        LOGGER.info ( "Modules loaded." );
        configManager.init ( );
        eventManager.init ( );
        LOGGER.info ( "EventManager loaded." );
        textManager.init ( true );
        moduleManager.onLoad ( );
        totemPopManager.init ( );
        timerManager.init ( );
        if ( moduleManager.getModuleByClass ( RPC.class ).isEnabled ( ) ) DiscordPresence.start();
        cosmeticsManager = new CosmeticsManager ( );
        LOGGER.info ( "AgalarHack initialized!\n" );
    }

    public static
    void unload ( boolean unload ) {
        LOGGER.info ( "\n\nUnloading AgalarHack 3.2.0" );
        if ( unload ) {
            reloadManager = new ReloadManager ( );
            reloadManager.init ( commandManager != null ? commandManager.getPrefix ( ) : "." );
        }
        if ( baritoneManager != null ) {
            baritoneManager.stop ( );
        }
        Agalar.onUnload ( );
        eventManager = null;
        holeManager = null;
        timerManager = null;
        moduleManager = null;
        totemPopManager = null;
        serverManager = null;
        colorManager = null;
        textManager = null;
        speedManager = null;
        rotationManager = null;
        positionManager = null;
        commandManager = null;
        configManager = null;
        fileManager = null;
        friendManager = null;
        potionManager = null;
        inventoryManager = null;
        notificationManager = null;
        safetyManager = null;
        LOGGER.info ( "AgalarHack unloaded!\n" );
    }

    public static
    void reload ( ) {
        Agalar.unload ( false );
        Agalar.load ( );
    }

    public static
    void onUnload ( ) {
        if ( ! unloaded ) {
            try {
                PhobosChat.INSTANCE.disconnect ( );
            } catch ( IOException e ) {
                e.printStackTrace ( );
            }
            eventManager.onUnload ( );
            moduleManager.onUnload ( );
            configManager.saveConfig ( Agalar.configManager.config.replaceFirst ( "Agalar/" , "" ) );
            moduleManager.onUnloadPost ( );
            timerManager.unload ( );
            unloaded = true;
        }
    }


    @Mod.EventHandler
    public
    void init ( FMLInitializationEvent event ) {
        customMainScreen = new GuiCustomMainScreen ( );
        Display.setTitle ( "Agalar - v.3.2.0" );
        Agalar.load ( );
    }
  /*  public static final String KEY = "Agalar";
    public static final String HWID_URL = "https://gist.githubusercontent.com/vhakan/17ea5369817dbdf3285b594c008d6134/raw/5b23efbdb6f1642884daec93dc08554acdd24b34/agalar.txt";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Verify();
    }
    public void Verify(){
        //Here we get the HWID List From URL
         hwidList = NetworkUtil.getHWIDList();

        //Check HWID
        if(!hwidList.contains(HWIDUtil.getEncryptedHWID(KEY))){
            //Shutdown client and display message
            FrameUtil.Display();
            throw new NoStackTraceThrowable("Verify HWID Failed!");*/

}

