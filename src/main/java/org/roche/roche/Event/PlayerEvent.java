package org.roche.roche.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PlayerEvent implements Listener {

    private final JavaPlugin plugin;
    private final Connection conn;

    public PlayerEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();

            ResultSet rs = selectPlayerInfo(player);
            if(rs == null) {
                insertPlayerInfo(player);
            } else {
                updatePlayerLastLoginTime(player);
            }

            plugin.getLogger().info(player.getName() + "님이 서버에 접속하셨습니다!");
            event.setJoinMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + "님이 서버에 접속하셨습니다");

            savePlayerEvent(player, "join");
        } catch (Exception e) {
            plugin.getLogger().severe("Player join event 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            plugin.getLogger().info(player.getName() + "님이 서버에서 나가셨습니다!");
            event.setQuitMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + "님이 서버에서 나가셨습니다");

            savePlayerEvent(player, "quit");
        } catch (Exception e) {
            plugin.getLogger().severe("Player quit event 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        // 웅크리기 + 왼손 변경
        if (player.isSneaking()) {
            event.setCancelled(true);

            // 메뉴 생성
            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "메뉴");

            ItemStack item = new ItemStack(Material.STONE); // 나무 막대 아이템 생성
            ItemMeta meta = item.getItemMeta();

            meta.setDisplayName("쓰레기통");
            meta.setLore(java.util.Arrays.asList("쓰레기통에 아이템을 넣고 닫으면 사라집니다."));

            inventory.setItem(0, item);
            inventory.setItem(1, item);
            inventory.setItem(9, item);
            inventory.setItem(10, item);

            player.openInventory(inventory);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            
            // 커스텀 아이템이면
            /*if (meta.hasDisplayName()) {
                player.sendMessage(ChatColor.RED + "[경고] " + ChatColor.WHITE + "이 아이템은 버릴 수 없습니다");
                event.setCancelled(true);
            }*/
        }

        savePlayerDropEvent(player, item);
    }

    private ResultSet selectPlayerInfo(Player player) {
        String query = "SELECT * FROM tb_player_info WHERE uuid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            return stmt.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().severe("플레이어 정보 조회 중 오류 발생: " + e.getMessage());
        }

        return null;
    }

    private void insertPlayerInfo(Player player) {
        String query = "INSERT INTO tb_player_info (uuid, player_name) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("플레이어 정보 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void updatePlayerLastLoginTime(Player player) {
        String query = "UPDATE tb_player_info SET last_login = NOW() WHERE uuid = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUniqueId().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("플레이어 마지막 접속 시간 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    private void savePlayerEvent(Player player, String event) {
        String query = "INSERT INTO tb_player_logs (player_name, event_type) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, event);

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("플레이어 이벤트 저장 중 오류 발생: " + e.getMessage());
        }
    }

    private void savePlayerDropEvent(Player player, ItemStack item) {
        String query = "INSERT INTO tb_player_drop_logs (player_name, item_name, item_meta) VALUES (?, ?, ?)";

        ItemMeta meta = item.getItemMeta();
        String metaString = null;

        if(meta != null) {
            metaString = meta.getAsString();
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, item.getItemMeta().getDisplayName());
            stmt.setString(3, metaString);

            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("플레이어 드롭 이벤트 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
