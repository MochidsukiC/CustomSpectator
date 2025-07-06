package jp.houlab.mochidsuki.customSpectator;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class Main extends JavaPlugin {

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    private static ProtocolManager protocolManager;
    public static Plugin plugin;

    @Override
    public void onEnable() {

        getCommand("customspectate").setExecutor(new CommandListener());

        plugin =this;

        protocolManager = ProtocolLibrary.getProtocolManager();


        /*

        // --- ここにプレイヤーを隠すコマンドなどを実装 ---
        // 例: hiddenPlayers.add(playerToHide.getUniqueId());


        protocolManager.addPacketListener(new PacketAdapter(this,
                // 監視対象のパケットタイプを更新・追加
                PacketType.Play.Server.NAMED_ENTITY_SPAWN,
                PacketType.Play.Server.PLAYER_INFO,
                PacketType.Play.Server.PLAYER_INFO_REMOVE,
                PacketType.Play.Server.ENTITY_METADATA,
                PacketType.Play.Server.ENTITY_HEAD_ROTATION,
                PacketType.Play.Server.ENTITY_TELEPORT,
                PacketType.Play.Server.REL_ENTITY_MOVE,
                PacketType.Play.Server.REL_ENTITY_MOVE_LOOK
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player receiver = event.getPlayer(); // パケットを受け取るプレイヤー
                // 不可視プレイヤー自身には通常通りパケットを送信
                if (GodInvisible.isInvisible(receiver.getUniqueId())) { // UUIDでチェック
                    return;
                }

                PacketContainer packet = event.getPacket();
                PacketType type = event.getPacketType();

                // プレイヤーのスポーン
                if (type == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                    UUID spawnedUUID = packet.getUUIDs().read(0);
                    if (GodInvisible.isInvisible(spawnedUUID)) { // UUIDでチェック
                        event.setCancelled(true);
                    }
                }
                // Tabリストなどの更新 (ADDアクション)
                else if (type == PacketType.Play.Server.PLAYER_INFO) {
                    // PlayerInfoDataリストから不可視プレイヤーを削除
                    List<PlayerInfoData> infoList = packet.getPlayerInfoDataLists().read(1);
                    infoList.removeIf(data -> GodInvisible.isInvisible(data.getProfile().getUUID())); // UUIDでチェック

                    if (infoList.isEmpty()) {
                        event.setCancelled(true);
                    } else {
                        packet.getPlayerInfoDataLists().write(1, infoList);
                    }
                }
                // Tabリストなどの更新 (REMOVEアクション)
                // 基本的にこのパケットは隠したいプレイヤーに関するものなので、そのまま通しても問題ないことが多い
                else if (type == PacketType.Play.Server.PLAYER_INFO_REMOVE) {
                    List<UUID> removedUuids = packet.getUUIDLists().read(0);
                    removedUuids.removeIf(GodInvisible::isInvisible); // 不可視プレイヤーをリストから除外

                    if(removedUuids.isEmpty()){
                        event.setCancelled(true);
                    } else {
                        packet.getUUIDLists().write(0, removedUuids);
                    }
                }
                // その他のエンティティ関連パケット (移動、向きなど)
                else if (type.name().startsWith("ENTITY_") || type.name().startsWith("REL_ENTITY_")) {
                    int entityId = packet.getIntegers().read(0);
                    // サーバーにいる全プレイヤーのIDと照合するのは非効率。
                    // ログイン時にUUIDとEntityIDをマップに保存しておくのが理想。
                    // ここでは簡易的な例としてBukkitのAPIを使う。
                    Player packetPlayer = null;
                    for(Player p : Bukkit.getOnlinePlayers()){
                        if(p.getEntityId() == entityId){
                            packetPlayer = p;
                            break;
                        }
                    }
                    if (packetPlayer != null && GodInvisible.isInvisible(packetPlayer.getUniqueId())) { // UUIDでチェック
                        event.setCancelled(true);
                    }
                }
            }
        });

         */
    }

    @Override
    public void onDisable() {
    }
}