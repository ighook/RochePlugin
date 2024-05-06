package org.roche.roche.Event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KillEvent implements Listener {

    private final JavaPlugin plugin;
    private final Connection conn;

    public KillEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // 공격을 받은 엔티티가 플레이어인 경우
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Entity damagedEntity = event.getEntity();

            // 플레이어가 다른 엔티티를 공격하고 해당 엔티티가 사망한 경우
            if (damagedEntity instanceof LivingEntity && ((LivingEntity) damagedEntity).getHealth() - event.getFinalDamage() <= 0) {
                String entityType = damagedEntity.getType().toString();
                if (entityType.equals("PLAYER")) {
                    entityType = "PLAYER";
                }

                savePlayerEvent(player, entityType);
            }
        }
    }

    private void savePlayerEvent(Player player, String entityType) {
        String query = "INSERT INTO tb_player_kill_logs (player_name, entity) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getName());
            stmt.setString(2, entityType);

            stmt.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().severe("Error saving player kill event to database: " + ex.getMessage());
        }
    }
}
