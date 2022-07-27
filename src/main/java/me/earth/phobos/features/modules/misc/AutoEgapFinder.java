/*package me.earth.phobos.features.modules.misc.;

import baritone.api.BaritoneAPI;
import me.earth.phobos.Agalar;
import me.earth.phobos.features.command.Command;
import io.ace.nordclient.event.PacketEvent;
import io.ace.nordclient.hacks.Hack;
import io.ace.nordclient.managers.RotationManager;
import io.ace.nordclient.mixin.accessor.ICPacketPlayer;
import io.ace.nordclient.mixin.accessor.ICPacketUseEntity;
import io.ace.nordclient.utilz.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;

import java.util.ArrayList;

public class AutoEGapFinder extends Hack {

    public Setting healthLog;
    public Setting baritoneRetry;
    public Setting logOnLowPick;
    public Setting switchPick;
    public Setting surface;
    public Setting spawnerFix;
    public Setting waterFix;

    public AutoEGapFinder() {
        super("AutoEGapFinder", Category.PLAYER, -1);
        CousinWare.INSTANCE.settingsManager.rSetting(healthLog = new Setting("LogHealth", this, 10, 0, 36, true, "AutoEGapFinderLogHealth"));
        CousinWare.INSTANCE.settingsManager.rSetting(baritoneRetry = new Setting("BaritoneDelay", this, 100, 20, 1000, true, "AutoEGapFinderBaritoneDelay"));
        CousinWare.INSTANCE.settingsManager.rSetting(logOnLowPick = new Setting("LogOnLowPick", this, true, "AutoEGapFinderLogOnLowPick"));
        CousinWare.INSTANCE.settingsManager.rSetting(switchPick = new Setting("SwitchPick", this, true, "AutoEGapFinderSwitchPick"));
        CousinWare.INSTANCE.settingsManager.rSetting(surface = new Setting("Surface", this, false, "AutoEGapFinderSurface"));
        CousinWare.INSTANCE.settingsManager.rSetting(spawnerFix = new Setting("SpawnerFix", this, true, "AutoEGapFiderSpawnerFix"));
        CousinWare.INSTANCE.settingsManager.rSetting(waterFix = new Setting("WaterFix", this, true, "AutoEGapWaterFix"));
    }
    public ArrayList<Integer> entityIdList = new ArrayList();
    public ArrayList<BlockPos> gapLocation = new ArrayList();
    public BlockPos lastGap;
    public boolean searchingInChest;
    private int delay = 0;
    private int baritoneAntiSpam = 0;
    private int delaySkip = 0;
    private boolean surfaced = false;
    private int lastEntId = 0;
    private int antiFail1 = 0;
    private int antiFail2 = 0;
    int i = 0;
    int j = 0;
    boolean switchingSlot = false;
    int delaySlot = 0;
    int mineDelay = 0;
    int amountMined = 0;

    public void onUpdate() {
        delay++;
        antiFail1++;
        antiFail2++;
        if (gapLocation.size() > 2) {
            if (gapLocation.get(0) == gapLocation.get(1)) gapLocation.remove(1);
        }
        baritoneAntiSpam++;
        if (!mc.isSingleplayer() && gapLocation.size() != 0) {

            for (TileEntity chest : mc.world.loadedTileEntityList) {
                if (chest instanceof TileEntityChest) {
                    if (mc.player.getDistance(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ()) < 50) {
                        if (!mc.world.getBlockState(new BlockPos(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ())).getBlock().equals(Blocks.CHEST) && !mc.world.getBlockState(new BlockPos(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ())).getBlock().equals(Blocks.RAIL)) {
                            gapLocation.remove(0);
                            Command.sendClientSideMessage("Chest is Not There Someone Prob jew'd it what a fag");
                            if (surface.getValBoolean()) BaritoneUtil.doCommand("surface");
                            else BaritoneUtil.goToPos(gapLocation.get(0));
                            Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                        }
                    }
                    if (mc.player.getDistance(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ()) < 3) {
                        searchingInChest = true;
                    } else {
                        antiFail1 = 0;
                    }
                }
                if (spawnerFix.getValBoolean()) {
                    if (chest instanceof TileEntityMobSpawner) {
                        for (Entity entity : mc.world.loadedEntityList) {
                            if (entity instanceof EntityZombie || entity instanceof EntitySkeleton || entity instanceof EntitySpider) {
                                if (entity.getDistance(chest.getPos().getX(), chest.getPos().getY(), chest.getPos().getZ()) < 8) {
                                    mc.world.removeEntity(entity);
                                }

                            }
                        }
                    }
                }
            }
            for (Entity chest : mc.world.getLoadedEntityList()) {
                if (chest instanceof EntityMinecartChest) {
                    if (mc.player.getDistance(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ()) < 3) {
                        searchingInChest = true;
                        if (searchingInChest && !(mc.currentScreen instanceof GuiChest)) {
                            CPacketUseEntity interactPacket = new CPacketUseEntity();
                            ((ICPacketUseEntity) interactPacket).setEntityId(chest.getEntityId());
                            ((ICPacketUseEntity) interactPacket).setEntityAction(CPacketUseEntity.Action.INTERACT);
                            ((ICPacketUseEntity) interactPacket).setHand(EnumHand.MAIN_HAND);
                            ((ICPacketUseEntity) interactPacket).setHitVec(mc.player.getLookVec());
                            mc.player.connection.sendPacket(interactPacket);
                        }

                    } else {
                        antiFail2 = 0;
                    }
                }
            }
            if (searchingInChest && !(mc.currentScreen instanceof GuiChest)) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(gapLocation.get(0), EnumFacing.UP, EnumHand.MAIN_HAND, 1, 1, 1));

            }
            if (searchingInChest && mc.currentScreen instanceof GuiChest) {
                delay++;
                if (delay > 20) {
                    isSpoofingAngles = false;
                    for (int i = 0; i < ((GuiChest) mc.currentScreen).inventorySlots.inventorySlots.size() - 36; i++) {
                        if (mc.player.openContainer.getSlot(i).getStack().getItem() == Items.GOLDEN_APPLE) {
                            for (int j = ((GuiChest) mc.currentScreen).inventorySlots.inventorySlots.size() - 36; j < ((GuiChest) mc.currentScreen).inventorySlots.inventorySlots.size(); j++) {
                                if (mc.player.openContainer.getSlot(j).getStack().getItem() == Items.GOLDEN_APPLE) {
                                    //mc.playerController.windowClick(((GuiChest) mc.currentScreen).inventorySlots.windowId, i, 0, ClickType.PICKUP, mc.player);
                                    //mc.playerController.windowClick(((GuiChest) mc.currentScreen).inventorySlots.windowId, j, 0, ClickType.PICKUP, mc.player);
                                    //Command.sendClientSideMessage(String.valueOf(j));
                                    // mc.player.connection.sendPacket(new CPacketClickWindow(((GuiChest) mc.currentScreen).inventorySlots.windowId, i, 0, ClickType.QUICK_MOVE, new ItemStack(Items.GOLDEN_APPLE), (short) 1));

                                }
                            }
                        }
                    }
                    searchingInChest = false;

                    gapLocation.remove(0);
                    delay = 0;
                }
            }
            //

            if (!BaritoneUtil.isPathFinding() && !searchingInChest && gapLocation.size() != 0 && baritoneAntiSpam > baritoneRetry.getValInt()) {
                if (surface.getValBoolean()) {
                    if (mc.player.posY < 50) {
                        BaritoneUtil.doCommand("surface");
                        Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                        baritoneAntiSpam = 0;
                    } else {
                        BaritoneUtil.goToPos(gapLocation.get(0));
                        Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                        baritoneAntiSpam = 0;
                    }
                } else {
                    BaritoneUtil.goToPos(gapLocation.get(0));
                    Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                    baritoneAntiSpam = 0;
                }
            }
            if (mc.player.getDistance(gapLocation.get(0).getX(), gapLocation.get(0).getY(), gapLocation.get(0).getZ()) < 3) {
                if (!BaritoneUtil.isPathFinding() && antiFail2 > 200 || antiFail1 > 200) {
                    if (surface.getValBoolean()) {
                        if (mc.player.posY < 50) {
                            BaritoneUtil.doCommand("surface");
                            Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                            baritoneAntiSpam = 0;
                        } else {
                            gapLocation.remove(0);
                            BaritoneUtil.goToPos(gapLocation.get(0));
                            Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                        }
                    } else {
                        gapLocation.remove(0);
                        BaritoneUtil.goToPos(gapLocation.get(0));
                        Command.sendClientSideMessage("Locations Left :" + (gapLocation.size() - 1));
                    }
                }
            }
        }
        if (waterFix.getValBoolean()) {
            if (!MotionUtil.isMoving() && mc.player.isInWater() && mc.world.getBiome(PlayerUtil.getPlayerPos()).equals(Biomes.OCEAN) || mc.world.getBiome(PlayerUtil.getPlayerPos()).equals(Biomes.DEEP_OCEAN)) {
                if (mc.world.getBlockState(PlayerUtil.getPlayerPos().down()).getBlock().equals(Blocks.GRAVEL) || mc.world.getBlockState(PlayerUtil.getPlayerPos().down()).getBlock().equals(Blocks.STONE)) {
                    mineDelay++;
                    if (mineDelay > 50 && amountMined < 3) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, PlayerUtil.getPlayerPos(), EnumFacing.UP));
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, PlayerUtil.getPlayerPos(), EnumFacing.UP));
                        amountMined++;
                        mineDelay = 0;

                    }
                    if (amountMined >= 3) {
                        if (mineDelay > 50) {
                            if (InventoryUtil.findBlockInHotbar(Blocks.COBBLESTONE) != -1) {
                                mc.player.inventory.currentItem = InventoryUtil.findBlockInHotbar(Blocks.COBBLESTONE);
                                BlockInteractionHelper.placeBlockScaffold(PlayerUtil.getPlayerPos().up().up());
                                mineDelay = 0;
                                amountMined = 0;
                            }
                        }
                    }
                }
            }
        }

        if (gapLocation.size() == 0 && !searchingInChest && !mc.isSingleplayer()) {
            mc.getConnection().handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("All EGaps Found")));
            this.disable();
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() < healthLog.getValInt() && !mc.isSingleplayer()) {
            mc.getConnection().handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Health Below " + healthLog.getValInt())));
            this.disable();
        }
        if (logOnLowPick.getValBoolean()) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                float green = ((float) mc.player.getHeldItemMainhand().getMaxDamage() - (float) mc.player.getHeldItemMainhand().getItemDamage()) / (float) mc.player.getHeldItemMainhand().getMaxDamage();
                float red = 1 - green;
                int dmg = 100 - (int) (red * 100);
                if (dmg < 4) {
                    mc.getConnection().handleDisconnect(new SPacketDisconnect((ITextComponent)new TextComponentString("Pickaxe Below 4 Durability ")));
                    this.disable();
                }
            }
        }
        if (!(mc.currentScreen instanceof GuiChest) && !BaritoneUtil.isPathFinding() && !searchingInChest && delaySkip > 200) {
            //gapLocation.remove(0);
            //BaritoneUtil.goToPos(gapLocation.get(0));
            delaySkip = 100;
        } else {
            delaySkip = 0;
        }
        if (switchPick.getValBoolean()) {
            for (int i = 36; i < 45; i++) {
                if (mc.player.openContainer.getInventory().get(i).getItem() instanceof ItemPickaxe) {
                    float green = ((float) mc.player.openContainer.getInventory().get(i).getMaxDamage() - (float) mc.player.openContainer.getInventory().get(i).getItemDamage()) / (float) mc.player.openContainer.getInventory().get(i).getMaxDamage();
                    float red = 1 - green;
                    int dmg = 100 - (int) (red * 100);
                    if (dmg < 10) {
                        for (int j = 9; j < 45; j++) {
                            if (mc.player.openContainer.getInventory().get(j).getItem() instanceof ItemPickaxe) {
                                float green1 = ((float) mc.player.openContainer.getInventory().get(j).getMaxDamage() - (float) mc.player.openContainer.getInventory().get(j).getItemDamage()) / (float) mc.player.openContainer.getInventory().get(j).getMaxDamage();
                                float red1 = 1 - green1;
                                int dmg1 = 100 - (int) (red1 * 100);
                                Command.sendClientSideMessage(String.valueOf(dmg1));
                                if (dmg1 > 10) {
                                    if (!switchingSlot) {
                                        switchSlot(j, i);
                                    } //
                                }
                            }
                        }
                    }

                }
            }
        }
        if (switchingSlot) {
            delaySlot++;
            if (delaySlot > 1) {
                mc.playerController.windowClick(0, j, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, j, 0, ClickType.PICKUP, mc.player);
                switchingSlot = false;
                delaySlot = 0;
            }
        } //

    }

    public void switchSlot(int j1, int i1) {
        i = i1;
        j = j1;
        switchingSlot = true;

    }


    @SubscribeEvent
    public void WorldTick(final TickEvent.WorldTickEvent event) {
        if (mc.isSingleplayer()) {
            final World world = event.world;
            EntityPlayer player = null;
            if (!world.playerEntities.isEmpty()) {
                player = world.playerEntities.get(0);
                for (final TileEntity tile : world.loadedTileEntityList) {
                    if (tile instanceof TileEntityLockableLoot) {
                        final TileEntityLockableLoot lockable = (TileEntityLockableLoot) tile;
                        lockable.fillWithLoot(player);
                        if (!gapLocation.contains(lockable.getPos()) && lastGap != lockable.getPos()) {
                            for (int i = 0; i < lockable.getSizeInventory(); ++i) {
                                final ItemStack stack = lockable.getStackInSlot(i);
                                if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() == 1) {
                                    BlockPos loc1 = lockable.getPos();
                                    lastGap = loc1;
                                    if (gapLocation.size() > 0) {
                                        if (gapLocation.get(gapLocation.size() - 1) != loc1) {
                                            gapLocation.add(loc1);
                                            Command.sendClientSideMessage("Dungeon with ench gapple at: " + loc1);
                                            Command.sendClientSideMessage("Gap Locations " + gapLocation.size());
                                        }
                                    } else {
                                        gapLocation.add(loc1);
                                        Command.sendClientSideMessage("Dungeon with ench gapple at: " + loc1);
                                        Command.sendClientSideMessage("Gap Locations " + gapLocation.size());
                                    }
                                }
                            }

                        }
                    }
                }
                for (final Entity entity : world.loadedEntityList) {
                    if (entity instanceof EntityMinecartContainer) {
                        final EntityMinecartContainer cart = (EntityMinecartContainer) entity;
                        cart.addLoot(player);
                        if (!gapLocation.contains(cart.getPosition()) && lastGap != cart.getPosition()) {
                            for (int i = 0; i < cart.itemHandler.getSlots(); ++i) {
                                final ItemStack stack = cart.itemHandler.getStackInSlot(i);
                                if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() == 1) {
                                    BlockPos loc = new BlockPos(cart.posX, cart.posY, cart.posZ);
                                    lastGap = loc;
                                    if (gapLocation.size() > 0) {
                                        if (gapLocation.get(gapLocation.size() - 1) != loc && !entityIdList.contains(entity.getEntityId())) {
                                            if (cart.getPosition() == gapLocation.get(gapLocation.size() - 1) || loc == gapLocation.get(gapLocation.size() - 1)) return;
                                            entityIdList.add(cart.getEntityId());
                                            gapLocation.add(loc);
                                            Command.sendClientSideMessage("Minecart with ench gapple at: " + loc);
                                            Command.sendClientSideMessage("Gap Locations " + gapLocation.size());
                                        }
                                    } else if (!entityIdList.contains(entity.getEntityId())){
                                        entityIdList.add(cart.getEntityId());
                                        gapLocation.add(loc);
                                        Command.sendClientSideMessage("Minecart with ench gapple at: " + loc);
                                        Command.sendClientSideMessage("Gap Locations " + gapLocation.size());
                                    }
//
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = RotationManager.calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private static boolean isSpoofingAngles = false;
    private static double yaw;
    private static double pitch;

    //this modifies packets being sent so no extra ones are made. NCP used to flag with "too many packets"
    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (isSpoofingAngles) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            isSpoofingAngles = false;
        }
    }


    @Listener
    public void onUpdate(PacketEvent.Send event) {
        Packet packet = event.getPacket();
        if (packet instanceof CPacketPlayer) {
            if (isSpoofingAngles) {
                ((ICPacketPlayer) packet).setYaw((float) yaw);
                ((ICPacketPlayer) packet).setPitch((float) pitch);
            }
        }
    }

    public void onEnable() {
        mineDelay = 0;
        amountMined = 0;
        if (mc.isSingleplayer()) {
            gapLocation.clear();
            searchingInChest = false;
            lastGap = new BlockPos(0, 0, 0);
        } else {
            searchingInChest = false;

        }
    }
}*/
