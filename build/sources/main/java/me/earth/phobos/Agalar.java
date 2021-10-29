package me.earth.phobos;

import me.earth.phobos.features.gui.custom.GuiCustomMainScreen;
import me.earth.phobos.features.modules.client.PhobosChat;
import me.earth.phobos.features.modules.misc.RPC;
import me.earth.phobos.manager.*;
import me.earth.phobos.util.Tracker;
import me.earth.phobos.util.TrackerID;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import java.io.IOException;

@Mod(modid = "agalar", name = "AgalarClient", version = "3.1.0")
public
class Agalar {
    public static final String MODID = "AgalarClient";
    public static final String MODNAME = "AgalarClient";
    public static final String MODVER = "3.1.0";
    public static final String NAME_UNICODE = "ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String PHOBOS_UNICODE = "ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String CHAT_SUFFIX = " \u23d0 ᴀɢᴀʟᴀʀ ᴄʟɪᴇɴᴛ";
    public static final String PHOBOS_SUFFIX = " \u23d0 \u1d18\u029c\u1d0f\u0299\u1d0f\ua731";
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

    static {
        unloaded = false;
    }

    public static
    void load ( ) {
        LOGGER.info ( "\n\nLoading AgalarClient 3.1.0" );
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
        LOGGER.info ( "\n\nUnloading AgalarHack 3.1.0" );
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
            new TrackerID ( );
            unloaded = true;
        }
    }

    @Mod.EventHandler
    public
    void preInit ( FMLPreInitializationEvent event ) {
        new Tracker ( );
        new ClassManager ( );
        LOGGER.info ( "ohare is cute!!!" );
        LOGGER.info ( "faggot above - 3vt" );
        LOGGER.info ( "megyn wins again" );
        LOGGER.info ( "gtfo my logs - 3arth" );
    }

    @Mod.EventHandler
    public
    void init ( FMLInitializationEvent event ) {
        customMainScreen = new GuiCustomMainScreen ( );
        Display.setTitle ( "Agalar - v.3.1.0" );
        Agalar.load ( );
    }
}