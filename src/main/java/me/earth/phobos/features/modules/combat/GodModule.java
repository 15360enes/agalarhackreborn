package me.earth.phobos.features.modules.combat;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public
class GodModule extends Module {
    public Setting rotations = this.register ( new Setting <> ( "Spoofs" , 1 , 1 , 20 ) );
    public Setting rotate = this.register ( new Setting <> ( "Rotate" , false ) );
    public Setting render = this.register ( new Setting <> ( "Render" , false ) );
    public Setting antiIllegal = this.register ( new Setting <> ( "AntiIllegal" , true ) );
    public Setting checkPos = this.register ( new Setting <> ( "CheckPos" , false ) );
    public Setting oneDot15 = this.register ( new Setting <> ( "1.15" , false ) );
    public Setting entitycheck = this.register ( new Setting <> ( "EntityCheck" , false ) );
    public Setting attacks = this.register ( new Setting <> ( "Attacks" , 1 , 1 , 10 ) );
    public Setting offset = this.register ( new Setting <> ( "Offset" , 0 , 0 , 2 ) );
    public Setting delay = this.register ( new Setting <> ( "Delay" , 0 , 0 , 250 ) );
    private float yaw;
    private float pitch;
    private boolean rotating;
    private int rotationPacketsSpoofed;
    private int highestID = - 100000;

    public
    GodModule ( ) {
        super ( "GodModule" , "Wow" , Module.Category.COMBAT , true , false , false );
    }

    public
    void onToggle ( ) {
        this.resetFields ( );
        if ( mc.world != null ) {
            this.updateEntityID ( );
        }

    }

    public
    void onUpdate ( ) {
        if ( (Boolean) this.render.getValue ( ) ) {

            for (Entity entity : mc.world.loadedEntityList) {
                if ( entity instanceof EntityItem ) return;
                if ( entity instanceof EntityPlayer ) return;
                if ( entity instanceof EntityEnderCrystal ) {
                    entity.setCustomNameTag ( String.valueOf ( entity.entityId ) );
                    entity.setAlwaysRenderNameTag ( true );
                }
            }
        }

    }

    public
    void onLogout ( ) {
        this.resetFields ( );
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public
    void onSendPacket ( PacketEvent.Send event ) {
        if ( event.getStage ( ) == 0 && event.getPacket ( ) instanceof CPacketPlayerTryUseItemOnBlock ) {
            CPacketPlayerTryUseItemOnBlock packet = event.getPacket ( );
            if ( mc.player.getHeldItem ( packet.hand ).getItem ( ) instanceof ItemEndCrystal ) {
                if ( (Boolean) this.checkPos.getValue ( ) && ! BlockUtil.canPlaceCrystal ( packet.position , (Boolean) this.entitycheck.getValue ( ) , (Boolean) this.oneDot15.getValue ( ) ) || this.checkPlayers ( ) ) {
                    return;
                }

                this.updateEntityID ( );

                for (int i = 1 - (Integer) this.offset.getValue ( ); i <= (Integer) this.attacks.getValue ( ); ++ i) {
                    this.attackID ( packet.position , this.highestID + i );
                }
            }
        }

        if ( event.getStage ( ) == 0 && this.rotating && (Boolean) this.rotate.getValue ( ) && event.getPacket ( ) instanceof CPacketPlayer ) {
            CPacketPlayer packet = event.getPacket ( );
            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
            ++ this.rotationPacketsSpoofed;
            if ( this.rotationPacketsSpoofed >= (Integer) this.rotations.getValue ( ) ) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }

    }

    private
    void attackID ( BlockPos pos , int id ) {
        Entity entity = mc.world.getEntityByID ( id );
        if ( entity instanceof EntityItem ) return;
        if ( entity instanceof EntityPlayer ) return;
        if ( entity == null || entity instanceof EntityEnderCrystal ) {
            GodModule.AttackThread attackThread = new GodModule.AttackThread ( id , pos , (Integer) this.delay.getValue ( ) , this );
            if ( (Integer) this.delay.getValue ( ) == 0 ) {
                attackThread.run ( );
            } else {
                attackThread.start ( );
            }
        }

    }

    @SubscribeEvent
    public
    void onPacketReceive ( PacketEvent.Receive event ) {
        if ( event.getPacket ( ) instanceof SPacketSpawnObject ) {
            this.checkID ( ( (SPacketSpawnObject) event.getPacket ( ) ).getEntityID ( ) );
        } else if ( event.getPacket ( ) instanceof SPacketSpawnExperienceOrb ) {
            this.checkID ( ( (SPacketSpawnExperienceOrb) event.getPacket ( ) ).getEntityID ( ) );
        } else if ( event.getPacket ( ) instanceof SPacketSpawnPlayer ) {
            this.checkID ( ( (SPacketSpawnPlayer) event.getPacket ( ) ).getEntityID ( ) );
        } else if ( event.getPacket ( ) instanceof SPacketSpawnGlobalEntity ) {
            this.checkID ( ( (SPacketSpawnGlobalEntity) event.getPacket ( ) ).getEntityId ( ) );
        } else if ( event.getPacket ( ) instanceof SPacketSpawnPainting ) {
            this.checkID ( ( (SPacketSpawnPainting) event.getPacket ( ) ).getEntityID ( ) );
        } else if ( event.getPacket ( ) instanceof SPacketSpawnMob ) {
            this.checkID ( ( (SPacketSpawnMob) event.getPacket ( ) ).getEntityID ( ) );
        }

    }

    private
    void checkID ( int id ) {
        if ( id > this.highestID ) {
            this.highestID = id;
        }

    }

    public
    void updateEntityID ( ) {

        for (Entity entity : mc.world.loadedEntityList) {
            if ( entity instanceof EntityItem ) return;
            if ( entity instanceof EntityPlayer ) return;
            if ( entity.getEntityId ( ) > this.highestID ) {
                this.highestID = entity.getEntityId ( );
            }
        }

    }

    private
    boolean checkPlayers ( ) {
        if ( (Boolean) this.antiIllegal.getValue ( ) ) {
            for (EntityPlayer player : GodModule.mc.world.playerEntities) {
                if ( player.getHealth ( ) > 0 && ! this.checkItem ( player.getHeldItemMainhand ( ) ) && ! this.checkItem ( player.getHeldItemOffhand ( ) ) )
                    continue;
                return false;
            }
        }
        return true;
    }

    private
    boolean checkItem ( ItemStack stack ) {
        return stack.getItem ( ) instanceof ItemBow || stack.getItem ( ) instanceof ItemExpBottle || stack.getItem ( ) == Items.STRING;
    }

    public
    void rotateTo ( BlockPos pos ) {
        float[] angle = MathUtil.calcAngle ( mc.player.getPositionEyes ( mc.getRenderPartialTicks ( ) ) , new Vec3d ( pos ) );
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
    }

    private
    void resetFields ( ) {
        this.rotating = false;
        this.highestID = - 1000000;
    }

    public static
    class AttackThread extends Thread {
        private final BlockPos pos;
        private final int id;
        private final int delay;
        private final GodModule godModule;

        public
        AttackThread ( int idIn , BlockPos posIn , int delayIn , GodModule godModuleIn ) {
            this.id = idIn;
            this.pos = posIn;
            this.delay = delayIn;
            this.godModule = godModuleIn;
        }

        public
        void run ( ) {
            try {
                if ( this.delay != 1 ) {
                    TimeUnit.MILLISECONDS.sleep ( this.delay );
                }

                Util.mc.addScheduledTask ( ( ) -> {
                    if ( ! Feature.fullNullCheck ( ) ) {
                        CPacketUseEntity attack = new CPacketUseEntity ( );
                        attack.entityId = this.id;
                        attack.action = Action.ATTACK;
                        this.godModule.rotateTo ( this.pos.up ( ) );
                        Util.mc.player.connection.sendPacket ( attack );
                        Util.mc.player.connection.sendPacket ( new CPacketAnimation ( EnumHand.MAIN_HAND ) );
                    }

                } );
            } catch ( InterruptedException var2 ) {
                var2.printStackTrace ( );
            }

        }
    }
}
