package jp.houlab.mochidsuki.customSpectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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

            // --- showPlayerの代わりとなる手動パケット送信処理 ---

            // 1. PlayerInfoリストに「追加」するパケットを作成
            PacketContainer playerInfoAddPacket = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            playerInfoAddPacket.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED));

            // ★修正点★ 現在のバージョンに合った正しいコンストラクタを使用
            PlayerInfoData playerInfoData = new PlayerInfoData(
                    WrappedGameProfile.fromPlayer(player),
                    player.getPing(),
                    // isListed引数を削除
                    EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                    null, // displayName - nullでクライアント側で解決させるのが安全
                    null  // chatSession
            );

            // ★修正点★ 正しいインデックス(0)を使用
            playerInfoAddPacket.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));

            // 2. プレイヤーをワールドに「スポーン」させるパケットを作成
            PacketContainer spawnPacket = protocolManager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
            // エンティティID
            spawnPacket.getIntegers().write(0, player.getEntityId());
            // UUID
            spawnPacket.getUUIDs().write(0, player.getUniqueId());
            // 座標
            spawnPacket.getDoubles()
                    .write(0, player.getLocation().getX())
                    .write(1, player.getLocation().getY())
                    .write(2, player.getLocation().getZ());
            // 角度
            spawnPacket.getBytes()
                    .write(0, (byte) (player.getLocation().getYaw() * 256.0F / 360.0F))
                    .write(1, (byte) (player.getLocation().getPitch() * 256.0F / 360.0F));

            // 3. サーバー上の全プレイヤーに上記パケットを送信
            broadcastPackets(player, protocolManager, playerInfoAddPacket, spawnPacket);
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