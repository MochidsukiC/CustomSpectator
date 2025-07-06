package jp.houlab.mochidsuki.customSpectator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

import static jp.houlab.mochidsuki.customSpectator.Main.plugin;

public class GodMode {
    private static final HashSet<UUID> GodModePlayerSet = new HashSet<>();
    public static void setGodMode(Player player, boolean godMode) {
        GodInvisible.setInvisible(player,godMode);

        if(plugin.getServer().getPluginManager().isPluginEnabled("GameMap")) {
            jp.houlab.mochidsuki.gamemap.Main.setEnemyVisible(player, godMode);
        }

            if(godMode){
            GodModePlayerSet.add(player.getUniqueId());
            if(plugin.getServer().getPluginManager().isPluginEnabled("Pin")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    jp.houlab.mochidsuki.pin.V.addGlowing(player, other);
                }
            }
        }else {
            GodModePlayerSet.remove(player.getUniqueId());

            if(plugin.getServer().getPluginManager().isPluginEnabled("Pin")) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if ((player.getScoreboard().getPlayerTeam(player) == null || !player.getScoreboard().getPlayerTeam(player).getEntries().contains(other.getName()))) {
                        jp.houlab.mochidsuki.pin.V.removeGlowing(player, other);
                    }
                }
            }
        }

    }

    public static boolean isGodMode(Player player) {
        return GodModePlayerSet.contains(player.getUniqueId());
    }
}
