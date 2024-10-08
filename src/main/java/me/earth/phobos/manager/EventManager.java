package me.earth.phobos.manager;

import com.google.common.base.Strings;
import me.earth.phobos.Agalar;
import me.earth.phobos.event.events.*;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.Management;
import me.earth.phobos.features.modules.client.PingBypass;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.util.GLUProjection;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public
class EventManager
        extends Feature {
    private final Timer timer = new Timer ( );
    private final Timer logoutTimer = new Timer ( );
    private final Timer switchTimer = new Timer ( );
    private final Timer chorusTimer = new Timer ( );
    private final AtomicBoolean tickOngoing = new AtomicBoolean ( false );
    private boolean keyTimeout;

    public
    void init ( ) {
        MinecraftForge.EVENT_BUS.register ( this );
    }

    public
    void onUnload ( ) {
        MinecraftForge.EVENT_BUS.unregister ( this );
    }

    @SubscribeEvent
    public
    void onUpdate ( LivingEvent.LivingUpdateEvent event ) {
        if ( ! EventManager.fullNullCheck ( ) && event.getEntity ( ).getEntityWorld ( ).isRemote && event.getEntityLiving ( ).equals ( EventManager.mc.player ) ) {
            Agalar.potionManager.update ( );
            Agalar.totemPopManager.onUpdate ( );
            Agalar.inventoryManager.update ( );
            Agalar.holeManager.update ( );
            Agalar.safetyManager.onUpdate ( );
            Agalar.moduleManager.onUpdate ( );
            Agalar.timerManager.update ( );
            if ( this.timer.passedMs ( Management.getInstance ( ).moduleListUpdates.getValue ( ) ) ) {
                Agalar.moduleManager.sortModules ( true );
                Agalar.moduleManager.alphabeticallySortModules ( );
                this.timer.reset ( );
            }
        }
    }

    @SubscribeEvent
    public
    void onSettingChange ( ClientEvent event ) {
        if ( event.getStage ( ) == 2 && mc.getConnection ( ) != null && PingBypass.getInstance ( ).isConnected ( ) && EventManager.mc.world != null ) {
            String command = "@Server" + PingBypass.getInstance ( ).getServerPrefix ( ) + "module " + event.getSetting ( ).getFeature ( ).getName ( ) + " set " + event.getSetting ( ).getName ( ) + " " + event.getSetting ( ).getPlannedValue ( ).toString ( );
            new CPacketChatMessage ( command );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public
    void onTickHighest ( TickEvent.ClientTickEvent event ) {
        if ( event.phase == TickEvent.Phase.START ) {
            this.tickOngoing.set ( true );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public
    void onTickLowest ( TickEvent.ClientTickEvent event ) {
        if ( event.phase == TickEvent.Phase.END ) {
            this.tickOngoing.set ( false );
            AutoCrystal.getInstance ( ).postTick ( );
        }
    }

    public
    boolean ticksOngoing ( ) {
        return this.tickOngoing.get ( );
    }

    @SubscribeEvent
    public
    void onClientConnect ( FMLNetworkEvent.ClientConnectedToServerEvent event ) {
        this.logoutTimer.reset ( );
        Agalar.moduleManager.onLogin ( );
    }

    @SubscribeEvent
    public
    void onClientDisconnect ( FMLNetworkEvent.ClientDisconnectionFromServerEvent event ) {
        Agalar.moduleManager.onLogout ( );
        Agalar.totemPopManager.onLogout ( );
        Agalar.potionManager.onLogout ( );
    }

    @SubscribeEvent
    public
    void onTick ( TickEvent.ClientTickEvent event ) {
        if ( EventManager.fullNullCheck ( ) ) {
            return;
        }
        Agalar.moduleManager.onTick ( );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public
    void onUpdateWalkingPlayer ( UpdateWalkingPlayerEvent event ) {
        if ( EventManager.fullNullCheck ( ) ) {
            return;
        }
        if ( event.getStage ( ) == 0 ) {
            Agalar.baritoneManager.onUpdateWalkingPlayer ( );
            Agalar.speedManager.updateValues ( );
            Agalar.rotationManager.updateRotations ( );
            Agalar.positionManager.updatePosition ( );
        }
        if ( event.getStage ( ) == 1 ) {
            Agalar.rotationManager.restoreRotations ( );
            Agalar.positionManager.restorePosition ( );
        }
    }

    @SubscribeEvent
    public
    void onPacketSend ( PacketEvent.Send event ) {
        if ( event.getPacket ( ) instanceof CPacketHeldItemChange ) {
            this.switchTimer.reset ( );
        }
    }

    public
    boolean isOnSwitchCoolDown ( ) {
        return ! this.switchTimer.passedMs ( 500L );
    }

    @SubscribeEvent
    public
    void onPacketReceive ( PacketEvent.Receive event ) {
        if ( event.getStage ( ) != 0 ) {
            return;
        }
        Agalar.serverManager.onPacketReceived ( );
        if ( event.getPacket ( ) instanceof SPacketEntityStatus ) {
            SPacketEntityStatus packet = event.getPacket ( );
            if ( packet.getOpCode ( ) == 35 && packet.getEntity ( EventManager.mc.world ) instanceof EntityPlayer ) {
                EntityPlayer player = (EntityPlayer) packet.getEntity ( EventManager.mc.world );
                MinecraftForge.EVENT_BUS.post ( new TotemPopEvent ( player ) );
                Agalar.totemPopManager.onTotemPop ( player );
                Agalar.potionManager.onTotemPop ( player );
            }
        } else if ( event.getPacket ( ) instanceof SPacketPlayerListItem && ! EventManager.fullNullCheck ( ) && this.logoutTimer.passedS ( 1.0 ) ) {
            SPacketPlayerListItem packet = event.getPacket ( );
            if ( ! SPacketPlayerListItem.Action.ADD_PLAYER.equals ( packet.getAction ( ) ) && ! SPacketPlayerListItem.Action.REMOVE_PLAYER.equals ( packet.getAction ( ) ) ) {
                return;
            }
            packet.getEntries ( ).stream ( ).filter ( Objects::nonNull ).filter ( data -> ! Strings.isNullOrEmpty ( data.getProfile ( ).getName ( ) ) || data.getProfile ( ).getId ( ) != null ).forEach ( data -> {
                UUID id = data.getProfile ( ).getId ( );
                switch (packet.getAction ( )) {
                    case ADD_PLAYER: {
                        String name = data.getProfile ( ).getName ( );
                        MinecraftForge.EVENT_BUS.post ( new ConnectionEvent ( 0 , id , name ) );
                        break;
                    }
                    case REMOVE_PLAYER: {
                        EntityPlayer entity = EventManager.mc.world.getPlayerEntityByUUID ( id );
                        if ( entity != null ) {
                            String logoutName = entity.getName ( );
                            MinecraftForge.EVENT_BUS.post ( new ConnectionEvent ( 1 , entity , id , logoutName ) );
                            break;
                        }
                        MinecraftForge.EVENT_BUS.post ( new ConnectionEvent ( 2 , id , null ) );
                    }
                }
            } );
        } else if ( event.getPacket ( ) instanceof SPacketTimeUpdate ) {
            Agalar.serverManager.update ( );
        } else if ( event.getPacket ( ) instanceof SPacketSoundEffect && ( (SPacketSoundEffect) event.getPacket ( ) ).getSound ( ) == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT ) {
            // chorus sends 2 sound packets, this is for ignoring the first one and also ignoring your own teleport,
            // which sends one packet which would break something like a simple "2nd packet" flag
            if ( ! chorusTimer.passedMs ( 100 ) )
                MinecraftForge.EVENT_BUS.post ( new ChorusEvent ( ( (SPacketSoundEffect) event.getPacket ( ) ).getX ( ) , ( (SPacketSoundEffect) event.getPacket ( ) ).getY ( ) , ( (SPacketSoundEffect) event.getPacket ( ) ).getZ ( ) ) );
            chorusTimer.reset ( );
        }
    }

    @SubscribeEvent
    public
    void onWorldRender ( RenderWorldLastEvent event ) {
        if ( event.isCanceled ( ) ) {
            return;
        }
        EventManager.mc.profiler.startSection ( "phobos" );
        GlStateManager.disableTexture2D ( );
        GlStateManager.enableBlend ( );
        GlStateManager.disableAlpha ( );
        GlStateManager.tryBlendFuncSeparate ( 770 , 771 , 1 , 0 );
        GlStateManager.shadeModel ( 7425 );
        GlStateManager.disableDepth ( );
        GlStateManager.glLineWidth ( 1.0f );
        Render3DEvent render3dEvent = new Render3DEvent ( event.getPartialTicks ( ) );
        GLUProjection projection = GLUProjection.getInstance ( );
        IntBuffer viewPort = GLAllocation.createDirectIntBuffer ( 16 );
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer ( 16 );
        FloatBuffer projectionPort = GLAllocation.createDirectFloatBuffer ( 16 );
        GL11.glGetFloat ( 2982 , modelView );
        GL11.glGetFloat ( 2983 , projectionPort );
        GL11.glGetInteger ( 2978 , viewPort );
        ScaledResolution scaledResolution = new ScaledResolution ( Minecraft.getMinecraft ( ) );
        projection.updateMatrices ( viewPort , modelView , projectionPort , (double) scaledResolution.getScaledWidth ( ) / (double) Minecraft.getMinecraft ( ).displayWidth , (double) scaledResolution.getScaledHeight ( ) / (double) Minecraft.getMinecraft ( ).displayHeight );
        Agalar.moduleManager.onRender3D ( render3dEvent );
        GlStateManager.glLineWidth ( 1.0f );
        GlStateManager.shadeModel ( 7424 );
        GlStateManager.disableBlend ( );
        GlStateManager.enableAlpha ( );
        GlStateManager.enableTexture2D ( );
        GlStateManager.enableDepth ( );
        GlStateManager.enableCull ( );
        GlStateManager.enableCull ( );
        GlStateManager.depthMask ( true );
        GlStateManager.enableTexture2D ( );
        GlStateManager.enableBlend ( );
        GlStateManager.enableDepth ( );
        EventManager.mc.profiler.endSection ( );
    }

    @SubscribeEvent
    public
    void renderHUD ( RenderGameOverlayEvent.Post event ) {
        if ( event.getType ( ) == RenderGameOverlayEvent.ElementType.HOTBAR ) {
            Agalar.textManager.updateResolution ( );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public
    void onRenderGameOverlayEvent ( RenderGameOverlayEvent.Text event ) {
        if ( event.getType ( ).equals ( RenderGameOverlayEvent.ElementType.TEXT ) ) {
            ScaledResolution resolution = new ScaledResolution ( mc );
            Render2DEvent render2DEvent = new Render2DEvent ( event.getPartialTicks ( ) , resolution );
            Agalar.moduleManager.onRender2D ( render2DEvent );
            GlStateManager.color ( 1.0f , 1.0f , 1.0f , 1.0f );
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public
    void onChatSent ( ClientChatEvent event ) {
        if ( event.getMessage ( ).startsWith ( Command.getCommandPrefix ( ) ) ) {
            event.setCanceled ( true );
            try {
                EventManager.mc.ingameGUI.getChatGUI ( ).addToSentMessages ( event.getMessage ( ) );
                if ( event.getMessage ( ).length ( ) > 1 ) {
                    Agalar.commandManager.executeCommand ( event.getMessage ( ).substring ( Command.getCommandPrefix ( ).length ( ) - 1 ) );
                } else {
                    Command.sendMessage ( "Please enter a command." );
                }
            } catch ( Exception e ) {
                e.printStackTrace ( );
                Command.sendMessage ( "\u00a7cAn error occurred while running this command. Check the log!" );
            }
            event.setMessage ( "" );
        }
    }
}

