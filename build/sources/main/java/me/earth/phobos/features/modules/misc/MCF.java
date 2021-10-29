package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Agalar;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.PingBypass;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public
class MCF
        extends Module {
    private final Setting < Boolean > middleClick = this.register ( new Setting <> ( "MiddleClick" , true ) );
    private final Setting < Boolean > keyboard = this.register ( new Setting <> ( "Keyboard" , false ) );
    private final Setting < Boolean > server = this.register ( new Setting <> ( "Server" , false ) );
    private final Setting < Boolean > sendmsg = this.register ( new Setting <> ( "Send Msgs" , false ) );
    private final Setting < Bind > key = this.register ( new Setting < Object > ( "KeyBind" , new Bind ( - 1 ) , v -> this.keyboard.getValue ( ) ) );
    private boolean clicked;

    public
    MCF ( ) {
        super ( "MCF" , "Middleclick Friends." , Module.Category.MISC , true , false , false );
    }

    @Override
    public
    void onUpdate ( ) {
        if ( Mouse.isButtonDown ( 2 ) ) {
            if ( ! this.clicked && this.middleClick.getValue ( ) && MCF.mc.currentScreen == null ) {
                this.onClick ( );
            }
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public
    void onKeyInput ( InputEvent.KeyInputEvent event ) {
        if ( this.keyboard.getValue ( ) && Keyboard.getEventKeyState ( ) && ! ( MCF.mc.currentScreen instanceof PhobosGui ) && this.key.getValue ( ).getKey ( ) == Keyboard.getEventKey ( ) ) {
            this.onClick ( );
        }
    }

    private
    void onClick ( ) {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if ( result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && ( entity = result.entityHit ) instanceof EntityPlayer ) {
            if ( Agalar.friendManager.isFriend ( entity.getName ( ) ) ) {
                Agalar.friendManager.removeFriend ( entity.getName ( ) );
                Command.sendMessage ( "\u00a7c" + entity.getName ( ) + "\u00a7r" + " unfriended." );
                if ( this.sendmsg.getValue ( ) ) {
                    mc.player.sendChatMessage ( "/msg " + entity.getName ( ) + " I have unadded u on Perry Phobos." );
                }
                if ( this.server.getValue ( ) && PingBypass.getInstance ( ).isConnected ( ) ) {
                    MCF.mc.player.connection.sendPacket ( new CPacketChatMessage ( "@Serverprefix" + ClickGui.getInstance ( ).prefix.getValue ( ) ) );
                    MCF.mc.player.connection.sendPacket ( new CPacketChatMessage ( "@Server" + ClickGui.getInstance ( ).prefix.getValue ( ) + "friend del " + entity.getName ( ) ) );
                }
            } else {
                Agalar.friendManager.addFriend ( entity.getName ( ) );
                Command.sendMessage ( "\u00a7b" + entity.getName ( ) + "\u00a7r" + " friended." );
                if ( this.sendmsg.getValue ( ) ) {
                    mc.player.sendChatMessage ( "/msg " + entity.getName ( ) + " I have added u on Perry Phobos." );
                }
                if ( this.server.getValue ( ) && PingBypass.getInstance ( ).isConnected ( ) ) {
                    MCF.mc.player.connection.sendPacket ( new CPacketChatMessage ( "@Serverprefix" + ClickGui.getInstance ( ).prefix.getValue ( ) ) );
                    MCF.mc.player.connection.sendPacket ( new CPacketChatMessage ( "@Server" + ClickGui.getInstance ( ).prefix.getValue ( ) + "friend add " + entity.getName ( ) ) );
                }
            }
        }
        this.clicked = true;
    }
}