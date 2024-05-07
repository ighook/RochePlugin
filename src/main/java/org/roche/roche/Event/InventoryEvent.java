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

    public InventoryEvent(JavaPlugin plugin, Connection conn) {
        this.plugin = plugin;
        this.conn = conn;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        String inventoryName = e.getView().getTitle();

        if (inventoryName.equals(ChatColor.WHITE + "\uF801\uEAAA")) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
                if (itemName.equals("쓰레기통")) {
                    p.closeInventory();

                    Inventory trashInventory = plugin.getServer().createInventory(null, 9, "쓰레기통");
                    p.openInventory(trashInventory);
                } else if (itemName.equals("강화")) {
                    p.closeInventory();

                    Inventory enchantInventory = plugin.getServer().createInventory(null, 27, "강화");

                    ItemStack item = new ItemStack(Material.GLASS_PANE);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("");

                    item.setItemMeta(meta);
                    for (int i = 0; i < 9; i++) {
                        enchantInventory.setItem(i, item);
                        enchantInventory.setItem(i + 18, item);
                    }
                    enchantInventory.setItem(9, item);
                    enchantInventory.setItem(11, item);
                    enchantInventory.setItem(12, item);
                    enchantInventory.setItem(14, item);
                    enchantInventory.setItem(15, item);
                    enchantInventory.setItem(17, item);

                    p.openInventory(enchantInventory);
                }
            }
        } else if (inventoryName.equals("강화")) {
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    int clickedSlot = e.getRawSlot();
                    if (clickedSlot == 16) {
                        if (e.getCursor().getType().toString().contains("SWORD")) {
                            plugin.getLogger().info("아이템 제작");

                            int diamondAmount = e.getInventory().getItem(13).getAmount();
                            if (diamondAmount > 5) {
                                e.getInventory().getItem(13).setAmount(e.getInventory().getItem(13).getAmount() - 5);
                            } else {
                                e.getInventory().setItem(13, null);
                            }
                            e.getInventory().setItem(10, null);


                            return;
                        }
                    }

                    boolean hasEquipment = false;
                    boolean hasMaterial = false;


                    plugin.getLogger().info("클릭한 슬롯: " + clickedSlot);

                    ItemStack item10 = e.getInventory().getItem(10);
                    if (item10 != null && item10.getType().toString().contains("SWORD")) {
                        plugin.getLogger().info("강화 아이템이 존재합니다.");
                        hasEquipment = true;
                    }


                    ItemStack item13 = e.getInventory().getItem(13);
                    if (item13 != null && item13.getType().equals(Material.DIAMOND)) {
                        plugin.getLogger().info("강화 재료가 존재합니다.");
                        hasMaterial = true;
                    }

                    if (hasEquipment && hasMaterial) {
                        plugin.getLogger().info("강화 아이템과 강화 재료가 모두 들어왔습니다");

                        int enchantLevel = 0;
                        int diamondAmount = e.getInventory().getItem(13).getAmount();
                        plugin.getLogger().info("강화 재료 다이아몬드 개수: " + diamondAmount);

                        if (diamondAmount > 0) {
                            enchantLevel = Math.min(diamondAmount, 5);

                            ItemStack newItem = new ItemStack(Material.DIAMOND_SWORD);

                            ItemMeta meta = newItem.getItemMeta();
                            meta.setDisplayName("강화된 다이아몬드 검");
                            meta.addEnchant(Enchantment.DAMAGE_ALL, enchantLevel, true);
                            newItem.setItemMeta(meta);

                            e.getInventory().setItem(16, newItem);
                        }
                    } else {
                        e.getInventory().setItem(16, null);
                    }


                }
            });
        }
    }

    private boolean isItemSword(ItemStack item) {
        return item != null && item.getType().toString().contains("SWORD");
    }

    private void handleItemCreation(InventoryClickEvent e, int materialSlot, int materialCost) {
        plugin.getLogger().info("아이템 제작");
        ItemStack materialItem = e.getInventory().getItem(materialSlot);
        if (materialItem.getAmount() > materialCost) {
            materialItem.setAmount(materialItem.getAmount() - materialCost);
        } else {
            e.getInventory().setItem(materialSlot, null);
        }
        e.getWhoClicked().getInventory().setItem(10, null);
    }

    private boolean checkAndLogItems(InventoryClickEvent e, int equipmentSlot, int materialSlot) {
        boolean hasEquipment = false, hasMaterial = false;

        ItemStack equipmentItem = e.getInventory().getItem(equipmentSlot);
        if (isItemSword(equipmentItem)) {
            plugin.getLogger().info("강화 아이템이 존재합니다.");
            hasEquipment = true;
        }

        ItemStack materialItem = e.getInventory().getItem(materialSlot);
        if (materialItem != null && materialItem.getType() == Material.DIAMOND) {
            plugin.getLogger().info("강화 재료가 존재합니다.");
            hasMaterial = true;
        }

        return hasEquipment && hasMaterial;
    }

    private void enhanceItem(InventoryClickEvent e, int equipmentSlot, int materialSlot, int maxEnchantLevel) {
        ItemStack materialItem = e.getInventory().getItem(materialSlot);
        int diamondAmount = materialItem.getAmount();
        plugin.getLogger().info("강화 재료 다이아몬드 개수: " + diamondAmount);

        int enchantLevel = Math.min(diamondAmount, maxEnchantLevel);
        ItemStack newItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = newItem.getItemMeta();
        meta.setDisplayName("강화된 다이아몬드 검");
        meta.addEnchant(Enchantment.DAMAGE_ALL, enchantLevel, true);
        newItem.setItemMeta(meta);

        e.getInventory().setItem(16, newItem);
    }
}
