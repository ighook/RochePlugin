package org.roche.roche.Event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;

public class InventoryEvent implements Listener {

    private final JavaPlugin plugin;
    private final Connection conn;

    private static final int DIAMOND_COST = 5;
    private static final int MAX_ENCHANT_LEVEL = 5;
    private static final int ENCHANTMENT_SLOT = 16;
    private static final String ENCHANTED_DIAMOND_SWORD_DISPLAY_NAME = "강화된 다이아몬드 검";
    private static final String TRASH_BIN_TITLE = "쓰레기통";
    private static final String ENCHANT_INVENTORY_TITLE = "강화";

    private Inventory trashBinGui;

    public InventoryEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;

        trashBinGui = Bukkit.createInventory(null, 9, TRASH_BIN_TITLE);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(ChatColor.WHITE + "\uF801\uEAAA")) {
            handleCustomInventoryClick(e);
        } else if (e.getView().getTitle().equals(ENCHANT_INVENTORY_TITLE)) {
            handleEnchantInventoryClick(e);
        } else if(e.getView().getTitle().equals(TRASH_BIN_TITLE)) {
            handleTrashBinClick(e);
        }
    }

    private void handleCustomInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        if (e.getCurrentItem() == null) return;
        String itemName = e.getCurrentItem().getItemMeta().getDisplayName();

        switch (itemName) {
            case "쓰레기통":
                player.openInventory(trashBinGui);
                break;
            case "강화":
                Inventory enchantInventory = createEnchantedInventory();
                player.openInventory(enchantInventory);
                break;
        }
    }

    private Inventory createEnchantedInventory() {
        Inventory enchantInventory = plugin.getServer().createInventory(null, 27, ENCHANT_INVENTORY_TITLE);
        ItemStack fillerItem = createFillerItem();

        for (int i = 0; i < 9; i++) {
            enchantInventory.setItem(i, fillerItem);
            enchantInventory.setItem(i + 18, fillerItem);
        }
        setEnchantInventoryBorders(enchantInventory, fillerItem);
        return enchantInventory;
    }

    private void setEnchantInventoryBorders(Inventory inventory, ItemStack fillerItem) {
        int[] borderSlots = {9, 11, 12, 14, 15, 17};
        for (int slot : borderSlots) {
            inventory.setItem(slot, fillerItem);
        }
    }

    private ItemStack createFillerItem() {
        ItemStack item = new ItemStack(Material.GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("");
        item.setItemMeta(meta);
        return item;
    }

    private void handleEnchantInventoryClick(InventoryClickEvent e) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            int clickedSlot = e.getRawSlot();
            if (clickedSlot != ENCHANTMENT_SLOT) return;

            if (isItemSword(e.getCursor())) {
                ItemStack materialItem = e.getInventory().getItem(13);
                reduceMaterialOrClear(materialItem, DIAMOND_COST);
                e.getInventory().setItem(10, null);
            } else if (hasRequiredItems(e.getInventory())) {
                enhanceItem(e.getInventory());
            } else {
                e.getInventory().setItem(ENCHANTMENT_SLOT, null);
            }
        });
    }

    private void handleTrashBinClick(InventoryClickEvent e) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for(int i = 0; i < 9; i++) {
                if(e.getRawSlot() == i) {
                    e.getInventory().setItem(i, null);
                    break;
                }
            }
        });
    }

    private boolean isItemSword(ItemStack item) {
        return item != null && item.getType().toString().contains("SWORD");
    }

    private boolean hasRequiredItems(Inventory inventory) {
        return isItemSword(inventory.getItem(10)) && inventory.getItem(13).getType() == Material.DIAMOND;
    }

    private void enhanceItem(Inventory inventory) {
        ItemStack materialItem = inventory.getItem(13);
        int diamondAmount = materialItem.getAmount();
        int enchantLevel = Math.min(diamondAmount, MAX_ENCHANT_LEVEL);

        ItemStack newItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = newItem.getItemMeta();
        meta.setDisplayName(ENCHANTED_DIAMOND_SWORD_DISPLAY_NAME);
        meta.addEnchant(Enchantment.DAMAGE_ALL, enchantLevel, true);
        newItem.setItemMeta(meta);

        inventory.setItem(ENCHANTMENT_SLOT, newItem);
    }

    private void reduceMaterialOrClear(ItemStack item, int cost) {
        int newAmount = item.getAmount() - cost;
        if (newAmount > 0) {
            item.setAmount(newAmount);
        } else {
            item.setType(Material.AIR);
        }
    }
}
