package org.roche.roche;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.roche.roche.Database.DatabaseConnector;
import org.roche.roche.Database.DatabaseManager;
import org.roche.roche.Event.BlockEvent;
import org.roche.roche.Event.KillEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Roche extends JavaPlugin implements Listener {
    private DatabaseManager databaseManager;
    private Connection conn;

    @Override
    public void onEnable() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = sdf.format(date);

        conn = DatabaseConnector.getConnection();

        if (conn != null) {
            getLogger().info("데이터베이스 연결 성공");
        } else {
            getLogger().severe("데이터베이스 연결 실패");
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new BlockEvent(this, conn), this);
        getServer().getPluginManager().registerEvents(new KillEvent(this, conn), this);
        getLogger().info("Roche 플러그인이 활성화되었습니다 [" + time + "]");
    }

    @Override
    public void onDisable() {
        getLogger().info("Roche 플러그인이 비활성화되었습니다");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            Player player = event.getPlayer();
            getLogger().info(player.getName() + "님이 서버에 접속하셨습니다!");
            event.setJoinMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + "님이 서버에 접속하셨습니다");

            savePlayerEvent(player, "join");
        } catch (Exception e) {
            getLogger().severe("Player join event 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            Player player = event.getPlayer();
            getLogger().info(player.getName() + "님이 서버에서 나가셨습니다!");
            event.setQuitMessage(ChatColor.AQUA + player.getName() + ChatColor.WHITE + "님이 서버에서 나가셨습니다");

            savePlayerEvent(player, "quit");
        } catch (Exception e) {
            getLogger().severe("Player quit event 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private void savePlayerEvent(Player player, String event) {
        String query = "INSERT INTO tb_player_logs (player_name, event_type) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, event);

            stmt.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe("플레이어 이벤트 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            event.setCancelled(true);
            player.sendMessage("You swapped hands while sneaking!");

//            ItemStack customItem = createCustomItem();
//            player.getInventory().setItemInMainHand(customItem);

            Inventory inventory = Bukkit.createInventory(null, 9, "");
            player.openInventory(inventory);

        }
    }

    private ItemStack createCustomItem() {
        ItemStack item = new ItemStack(Material.STICK); // 나무 막대 아이템 생성
        ItemMeta meta = item.getItemMeta();

        // 아이템의 이름과 설명 설정
        meta.setDisplayName("Custom Stick");
        meta.setLore(java.util.Arrays.asList("This is a custom stick."));

        // 아이템 메타 데이터 설정
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                // 커스텀 아이템인 경우
                player.sendMessage(ChatColor.RED + "[경고] " + ChatColor.WHITE + "이 아이템은 버릴 수 없습니다");
                event.setCancelled(true);
            }
        }
    }
}
