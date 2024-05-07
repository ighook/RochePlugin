package org.roche.roche.Event;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class InventoryEvent implements Listener {

    private final JavaPlugin plugin;
    private final Connection conn;

    public InventoryEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String inventoryName = e.getView().getTitle();

        if(inventoryName.equals("메뉴")) {
            e.setCancelled(true);

            if(e.getCurrentItem() != null)  {
                String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
                if(itemName.equals("쓰레기통")) {
                    p.closeInventory();

                    Inventory trashInventory = plugin.getServer().createInventory(null, 9, "쓰레기통");
                    p.openInventory(trashInventory);
                }
            }
        }
    }
}
