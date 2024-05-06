package org.roche.roche.Event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockEvent implements Listener {

    private final JavaPlugin plugin;
    private final Connection conn;

    public BlockEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        Player p = e.getPlayer();
        saveBlockBreakEvent(p, e.getBlock());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        saveBlockPlaceEvent(p, e.getBlock());
    }

    private void saveBlockBreakEvent(Player p, Block b) {
        String query = "INSERT INTO tb_block_break_logs (player_name, block_type, location_x, location_y, location_z, world) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, b.getType().toString());
            stmt.setDouble(3, b.getLocation().getX());
            stmt.setDouble(4, b.getLocation().getY());
            stmt.setDouble(5, b.getLocation().getZ());
            stmt.setString(6, b.getWorld().getName());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error saving block break event to database: " + ex.getMessage());
        }
    }

    private void saveBlockPlaceEvent(Player p, Block b) {
        String query = "INSERT INTO tb_block_place_logs (player_name, block_type, location_x, location_y, location_z, world) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, p.getName());
            stmt.setString(2, b.getType().toString());
            stmt.setDouble(3, b.getLocation().getX());
            stmt.setDouble(4, b.getLocation().getY());
            stmt.setDouble(5, b.getLocation().getZ());
            stmt.setString(6, b.getWorld().getName());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error saving block place event to database: " + ex.getMessage());
        }
    }
}
