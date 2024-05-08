package org.roche.roche.Controller;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryController {

    public Inventory createInventory(String title, int size) {
        Inventory inventory = Bukkit.createInventory(null, size, title);
        return inventory;
    }
}
