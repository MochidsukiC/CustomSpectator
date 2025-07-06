package jp.houlab.mochidsuki.customSpectator;

import jp.houlab.mochidsuki.hPDisplay.headDisplay.HeadDisplayMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

import static jp.houlab.mochidsuki.customSpectator.Main.plugin;

public class GodMode {
    private static final HashSet<UUID> GodModePlayerSet = new HashSet<>();

    public static HashSet<UUID> getGodModePlayerSet(){
        return GodModePlayerSet;
    }
    public static void setGodMode(Player player, boolean godMode) {
        GodInvisible.setInvisible(player, godMode);

        if (plugin.getServer().getPluginManager().isPluginEnabled("GameMap")) {
            jp.houlab.mochidsuki.gamemap.Main.setEnemyVisible(player, godMode);
        }

        if (godMode) {
            GodModePlayerSet.add(player.getUniqueId());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!player.getUniqueId().equals(other.getUniqueId())) {
                    if (plugin.getServer().getPluginManager().isPluginEnabled("Pin")) {
                        jp.houlab.mochidsuki.pin.Utilities.addGlowing(player, other);
                    }
                    if(plugin.getServer().getPluginManager().isPluginEnabled("HPDisplay")){
                        HeadDisplayMain.PLAYER_HEAD_DISPLAY_MAP.get(other.getUniqueId()).addShowPlayer(player);
                    }
                }
            }

        } else {
            GodModePlayerSet.remove(player.getUniqueId());

            for (Player other : Bukkit.getOnlinePlayers()) {
                if (plugin.getServer().getPluginManager().isPluginEnabled("Pin")) {
                    jp.houlab.mochidsuki.pin.Utilities.removeGlowing(player, other);
                }
                if (plugin.getServer().getPluginManager().isPluginEnabled("HPDisplay")) {
                    HeadDisplayMain.PLAYER_HEAD_DISPLAY_MAP.get(other.getUniqueId()).addHidePlayer(player);

                }
            }
        }

    }

    public static boolean isGodMode(Player player) {
        return GodModePlayerSet.contains(player.getUniqueId());
    }
}
