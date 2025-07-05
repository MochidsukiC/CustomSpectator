package jp.houlab.mochidsuki.customSpectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static jp.houlab.mochidsuki.customSpectator.Main.getProtocolManager;

public class GodInvisible {
    // プレイヤー名(String)ではなく、不変のUUIDで管理する
    private final static HashSet<UUID> godInvisiblePlayerList = new HashSet<>();

    public static void setInvisible(Player player, boolean invisible) {
        // ProtocolManagerのインスタンスをメソッドの最初で一度だけ取得する
        ProtocolManager protocolManager = getProtocolManager();

        if (invisible) {
            godInvisiblePlayerList.add(player.getUniqueId());

            // ★修正点★ 安全なパケット生成方法に変更
            // 1. エンティティ破壊パケットを作成
            PacketContainer destroyEntityPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            destroyEntityPacket.getIntLists().write(0, List.of(player.getEntityId()));

            // 2. PlayerInfoリストから削除するパケットを作成
            PacketContainer playerInfoRemovePacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
            playerInfoRemovePacket.getUUIDLists().write(0, List.of(player.getUniqueId()));

            // サーバー上の全プレイヤーにパケットを送信
            broadcastPackets(player, protocolManager, destroyEntityPacket, playerInfoRemovePacket);

        } else {
            godInvisiblePlayerList.remove(player.getUniqueId());

        }
    }

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