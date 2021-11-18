package me.earth.phobos.features.modules.misc;

import me.earth.phobos.DiscordPresence;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public
class RPC
        extends Module {
    public static RPC INSTANCE;
    public Setting < Boolean > lokummode = this.register ( new Setting <> ( "LokumMode" , false ) );
    public Setting < Boolean > showIP = this.register ( new Setting <> ( "ShowIP" , Boolean.TRUE , "Shows the server IP in your discord presence." ) );
    public Setting < String > state = this.register ( new Setting <> ( "State" , "AgalarClient 3.1.0" , "Sets the state of the DiscordRPC." ) );

    public
    RPC ( ) {
        super ( "RPC" , "Discord zengin var olus" , Module.Category.MISC , true , false , true );
        INSTANCE = this;
    }

    @Override
    public
    void onEnable ( ) {
        DiscordPresence.start ( );
    }

    @Override
    public
    void onDisable ( ) {
        DiscordPresence.start();
    }
}

