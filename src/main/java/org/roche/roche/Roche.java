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
import org.roche.roche.Event.InventoryEvent;
import org.roche.roche.Event.KillEvent;
import org.roche.roche.Event.PlayerEvent;

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
        getServer().getPluginManager().registerEvents(new PlayerEvent(this, conn), this);
        getServer().getPluginManager().registerEvents(new InventoryEvent(this, conn), this);
        getLogger().info("Roche 플러그인이 활성화되었습니다 [" + time + "]");
    }

    @Override
    public void onDisable() {
        getLogger().info("Roche 플러그인이 비활성화되었습니다");
    }


}
