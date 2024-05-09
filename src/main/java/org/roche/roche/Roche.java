package org.roche.roche;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.roche.roche.Command.CreateResourceWorldCommand;
import org.roche.roche.Command.MoveWorldCommand;
import org.roche.roche.Database.DatabaseConnector;
import org.roche.roche.Database.DatabaseManager;
import org.roche.roche.Event.BlockEvent;
import org.roche.roche.Event.InventoryEvent;
import org.roche.roche.Event.KillEvent;
import org.roche.roche.Event.PlayerEvent;

import java.sql.Connection;
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

        getCommand("createResourceWorld").setExecutor(new CreateResourceWorldCommand());
        getCommand("moveWorld").setExecutor(new MoveWorldCommand());
        getLogger().info("Roche 플러그인이 활성화되었습니다 [" + time + "]");


    }

    @Override
    public void onDisable() {
        getLogger().info("Roche 플러그인이 비활성화되었습니다");
    }


}
