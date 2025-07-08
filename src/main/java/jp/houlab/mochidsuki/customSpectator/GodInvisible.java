package jp.houlab.mochidsuki.customSpectator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static jp.houlab.mochidsuki.customSpectator.Main.getProtocolManager;
import static jp.houlab.mochidsuki.customSpectator.Main.plugin;

public class GodInvisible {
    // プレイヤー名(String)ではなく、不変のUUIDで管理する
    private final static HashSet<UUID> godInvisiblePlayerList = new HashSet<>();

    public static void setInvisible(Player player, boolean invisible) {
        // ProtocolManagerのインスタンスをメソッドの最初で一度だけ取得する
        //ProtocolManager protocolManager = getProtocolManager();

        if (invisible) {
            //不可視になる
            //不可視リストに追加
            godInvisiblePlayerList.add(player.getUniqueId());
            //全プレイヤーに不可視になったことを伝達
            for(Player otherPlayer : Bukkit.getOnlinePlayers()){
                //自分からは全員可視にする
                player.showPlayer(plugin,otherPlayer);

                if(!otherPlayer.getUniqueId().equals(player.getUniqueId()) && !GodInvisible.isInvisible(otherPlayer)) {
                    //可視プレイヤーから見えなくする
                    otherPlayer.hidePlayer(plugin, player);
                }else {
                    //不可視プレイヤーから見えるようにする
                    otherPlayer.showPlayer(plugin,player);
                }
            }

            /*
            // 1. エンティティ破壊パケットを作成
            PacketContainer destroyEntityPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyEntityPacket.getIntLists().write(0, List.of(player.getEntityId()));

            // 2. PlayerInfoリストから削除するパケットを作成
            PacketContainer playerInfoRemovePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
            playerInfoRemovePacket.getUUIDLists().write(0, List.of(player.getUniqueId()));

            // サーバー上の全プレイヤーにパケットを送信
            broadcastPackets(player, protocolManager, destroyEntityPacket, playerInfoRemovePacket);

             */
        } else {
            godInvisiblePlayerList.remove(player.getUniqueId());

            for(Player otherPlayer : Bukkit.getOnlinePlayers()){
                if(!otherPlayer.getUniqueId().equals(player.getUniqueId())) {
                    otherPlayer.showPlayer(plugin, player);
                }
            }
            for(UUID godPlayerUUID : GodMode.getGodModePlayerSet()){
                if(Bukkit.getOfflinePlayer(godPlayerUUID).isOnline()) {
                    player.hidePlayer(plugin,Bukkit.getPlayer(godPlayerUUID));
                }
            }
        }
    }

    /*
    // パケットをブロードキャストするためのヘルパーメソッド
    private static void broadcastPackets(Player subject, ProtocolManager manager, PacketContainer... packets) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getUniqueId().equals(subject.getUniqueId())) {
                try {
                    for (PacketContainer packet : packets) {
                        manager.sendServerPacket(onlinePlayer, packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

     */

    // Playerオブジェクトで判定するバージョン
    public static boolean isInvisible(Player player) {
        if (player == null) return false;
        return godInvisiblePlayerList.contains(player.getUniqueId());
    }

    // UUIDで直接判定するバージョン (オーバーロード)
    public static boolean isInvisible(UUID uuid) {
        if (uuid == null) return false;
        return godInvisiblePlayerList.contains(uuid);
    }
}