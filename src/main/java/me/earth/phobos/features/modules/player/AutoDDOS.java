/*package me.earth.phobos.features.modules.player;


import me.earth.phobos.Agalar;
import me.earth.phobos.util.*;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.features.modules.Module;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AutoDDOS extends Module {
    public static HashMap<String, Integer> TotemPopContainer = new HashMap<>();

    private static AutoDDOS INSTANCE = new AutoDDOS();

    public Setting<Boolean> load = register(new Setting("Load", Boolean.valueOf(false)));

    private Setting<Boolean> friends = register(new Setting("NoFriends", Boolean.valueOf(true)));

    private Setting<Boolean> safety = register(new Setting("Safety", Boolean.valueOf(false)));

    private Setting<Integer> safeFactor = register(new Setting("StopHealth", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15), v -> ((Boolean)this.safety.getValue()).booleanValue()));

    public AutoDDOS() {
        super("Stresser", "THEY CHINESE LAGGED ME", Category.MISC, true, false, false);
        setInstance();
    }

    public static AutoDDOS getInstance() {
        if (INSTANCE == null)
            INSTANCE = new AutoDDOS();
        return INSTANCE;
    }

    public void onUpdate() {
        if (((Boolean)this.load.getValue()).booleanValue()) {
            IMinecraftUtil.Util.mc.player.sendChatMessage("/msg " + mc.getSession().getUsername() + "aaaaaaaaaaaaa");
            this.load.setValue(Boolean.valueOf(false));
        }
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        TotemPopContainer.clear();
    }

    public void onTotemPop(EntityPlayer player) {
        if (fullNullCheck())
            return;
        if (mc.player.equals(player))
            return;
        if (((Boolean)this.safety.getValue()).booleanValue() && EntityUtil.getHealth((Entity)mc.player) <= ((Integer)this.safeFactor.getValue()).intValue())
            return;
        if (((Boolean)this.friends.getValue()).booleanValue() && Agalar.friendManager.isFriend(player.getName()))
            return;
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getName())) {
            l_Count = ((Integer)TotemPopContainer.get(player.getName())).intValue();
            TotemPopContainer.put(player.getName(), Integer.valueOf(++l_Count));
        } else {
            TotemPopContainer.put(player.getName(), Integer.valueOf(l_Count));
        }
        if (l_Count == 1) {
            IMinecraftUtil.Util.mc.player.sendChatMessage("/msg " + player.getName() + "aaaaaaaaaaaaa" );
        } else {
            IMinecraftUtil.Util.mc.player.sendChatMessage("/msg " + player.getName() + "aaaaaaaaaaaaa" );
        }
    }
}*/