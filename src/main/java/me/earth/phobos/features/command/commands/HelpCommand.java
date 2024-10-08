package me.earth.phobos.features.command.commands;

import me.earth.phobos.Agalar;
import me.earth.phobos.features.command.Command;

public
class HelpCommand
        extends Command {
    public
    HelpCommand ( ) {
        super ( "help" );
    }

    @Override
    public
    void execute ( String[] commands ) {
        HelpCommand.sendMessage ( "You can use following commands: " );
        for (Command command : Agalar.commandManager.getCommands ( )) {
            HelpCommand.sendMessage ( Agalar.commandManager.getPrefix ( ) + command.getName ( ) );
        }
    }
}

