package jp.houlab.mochidsuki.customSpectator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("customspectate")){
            GodInvisible.setInvisible((Player)commandSender,!GodInvisible.isInvisible((Player)commandSender) );
        }
        return false;
    }
}
