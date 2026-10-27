package dev.forever.origins;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static dev.forever.origins.ForeverOrigins.mm;

public class MenuManager implements Listener {
    private static final int[] SLOTS = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
    private static final int INFO_SLOT = 4;
    private static final int BOTTOM_SLOT = 31;
    private static final int BACK_SLOT = 30;
    private static final int CONFIRM_SLOT = 32;

    private enum Kind { PICK, PREVIEW, ABILITIES }

    private static class MenuHolder implements InventoryHolder {
        final Kind kind;
        final Origin origin;
        Inventory inv;
        MenuHolder(Kind kind, Origin origin) { this.kind = kind; this.origin = origin; }
        @Override public Inventory getInventory() { return inv; }
    }

    private final ForeverOrigins plugin;
    private final PlayerStore store;
    private final AbilityManager abilities;

    public MenuManager(ForeverOrigins plugin, PlayerStore store, AbilityManager abilities) {
        this.plugin = plugin;
        this.store = store;
        this.abilities = abilities;
    }

    // ---------- items ----------

    private ItemStack item(Material m, String name, List<String> lore, boolean glow) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(mm(name));
        List<Component> l = new ArrayList<>();
        for (String s : lore) l.add(mm(s));
        meta.lore(l);
        if (glow) meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        it.setItemMeta(meta);
        return it;
    }

    private void decorate(Inventory inv, Origin o) {
        Material border = Material.MAGENTA_STAINED_GLASS_PANE;
        if (o != null) {
            border = switch (o) {
                case PHOENIX -> Material.ORANGE_STAINED_GLASS_PANE;
                case FROSTBORN -> Material.LIGHT_BLUE_STAINED_GLASS_PANE;
                case AVIAN -> Material.CYAN_STAINED_GLASS_PANE;
                case VERDANT -> Material.LIME_STAINED_GLASS_PANE;
                case UMBRA -> Material.PURPLE_STAINED_GLASS_PANE;
                case TIDEBORN -> Material.BLUE_STAINED_GLASS_PANE;
                case STONEBORN -> Material.GRAY_STAINED_GLASS_PANE;
                case ARCANE -> Material.MAGENTA_STAINED_GLASS_PANE;
                case BEASTKIN -> Material.YELLOW_STAINED_GLASS_PANE;
                case CELESTIAL -> Material.WHITE_STAINED_GLASS_PANE;
            };
        }
        for (int i = 0; i < inv.getSize(); i++) {
            int row = i / 9, col = i % 9;
            boolean edge = row == 0 || row == 3 || col == 0 || col == 8;
            inv.setItem(i, item(edge ? border : Material.BLACK_STAINED_GLASS_PANE, " ", List.of(), false));
        }
    }

    private String fmt(double d) { return String.format("%.1f", d); }

    private ItemStack abilityItem(Ability a, boolean selected, boolean clickable) {
        List<String> lore = new ArrayList<>();
        lore.add("<gray>" + a.description());
        lore.add("");
        if (a.active()) {
            lore.add("<gold>⚡ ACTIVE <gray>• Cooldown <white>" + fmt(a.cooldown()) + "s");
            if (clickable) lore.add(selected ? "<green>✔ SELECTED" : "<yellow>Click to select");
        } else {
            lore.add("<aqua>◆ PASSIVE <gray>• always on");
        }
        Origin o = a.origin();
        return item(a.icon(), "<gradient:" + o.color() + ":" + o.color2() + "><bold>" + a.label() + "</bold></gradient>",
                lore, selected);
    }

    public ItemStack wandItem() {
        ItemStack it = item(Material.NETHER_STAR, "<gradient:#f6d365:#fda085><bold>Origin Wand</bold></gradient>",
                List.of("<gray>Right-click: <white>use selected ability",
                        "<gray>Left-click: <white>next active ability",
                        "<gray>Sneak + right-click: <white>open menu"), true);
        ItemMeta meta = it.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.wandKey, PersistentDataType.BYTE, (byte) 1);
        it.setItemMeta(meta);
        return it;
    }

    public boolean isWand(ItemStack it) {
        return it != null && it.hasItemMeta()
                && it.getItemMeta().getPersistentDataContainer().has(plugin.wandKey, PersistentDataType.BYTE);
    }

    public void giveWand(Player p) {
        for (ItemStack it : p.getInventory().getContents()) if (isWand(it)) return;
        p.getInventory().addItem(wandItem()).values()
                .forEach(left -> p.getWorld().dropItemNaturally(p.getLocation(), left));
    }

    // ---------- menus ----------

    public void openPick(Player p) {
        MenuHolder h = new MenuHolder(Kind.PICK, null);
        Inventory inv = Bukkit.createInventory(h, 36,
                mm("<gradient:#f6d365:#fda085><bold>✦ Choose Your Origin ✦</bold></gradient>"));
        h.inv = inv;
        decorate(inv, null);

        Origin[] all = Origin.values();
        for (int i = 0; i < all.length; i++) {
            Origin o = all[i];
            List<String> lore = new ArrayList<>();
            lore.add("<gray>" + o.description());
            lore.add("");
            lore.add("<color:" + o.color() + ">10 abilities <gray>(5 passive + 5 active)");
            lore.add("");
            lore.add("<yellow>Click to preview");
            inv.setItem(SLOTS[i], item(o.icon(), o.title(), lore, false));
        }
        inv.setItem(INFO_SLOT, item(Material.NETHER_STAR,
                "<gradient:#f6d365:#fda085><bold>Pick Your Origin</bold></gradient>",
                List.of("<gray>Every origin has <white>10 abilities", "<gray>and you get them all from the start."), true));
        inv.setItem(BOTTOM_SLOT, item(Material.PAPER, "<red><bold>This choice is permanent!</bold>",
                List.of("<gray>Preview each origin before you decide."), false));
        p.openInventory(inv);
    }

    public void openPreview(Player p, Origin o) {
        MenuHolder h = new MenuHolder(Kind.PREVIEW, o);
        Inventory inv = Bukkit.createInventory(h, 36, mm(o.title() + " <dark_gray>• Preview"));
        h.inv = inv;
        decorate(inv, o);
        inv.setItem(INFO_SLOT, item(o.icon(), o.title(), List.of("<gray>" + o.description()), true));
        List<Ability> list = o.abilities();
        for (int i = 0; i < list.size(); i++) inv.setItem(SLOTS[i], abilityItem(list.get(i), false, false));
        inv.setItem(BACK_SLOT, item(Material.ARROW, "<red><bold>« Back</bold>", List.of("<gray>See the other origins."), false));
        inv.setItem(CONFIRM_SLOT, item(Material.EMERALD_BLOCK, "<green><bold>Become " + o.label() + "</bold>",
                List.of("<gray>Choose this origin <red>forever<gray>."), true));
        p.openInventory(inv);
    }

    public void openAbilities(Player p, Origin o) {
        MenuHolder h = new MenuHolder(Kind.ABILITIES, o);
        Inventory inv = Bukkit.createInventory(h, 36, mm(o.title() + " <dark_gray>• Abilities"));
        h.inv = inv;
        decorate(inv, o);
        inv.setItem(INFO_SLOT, item(o.icon(), o.title(),
                List.of("<gray>" + o.description(), "", "<gray>Click an <gold>ACTIVE <gray>ability to select it."), true));
        Ability sel = abilities.selected(p);
        List<Ability> list = o.abilities();
        for (int i = 0; i < list.size(); i++) {
            Ability a = list.get(i);
            inv.setItem(SLOTS[i], abilityItem(a, a == sel, true));
        }
        inv.setItem(BOTTOM_SLOT, item(Material.BLAZE_ROD, "<gold><bold>Get Origin Wand</bold>",
                List.of("<gray>Click to receive the wand", "<gray>used to cast your abilities."), false));
        p.openInventory(inv);
    }

    // ---------- events ----------

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof MenuHolder h)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() == null || e.getClickedInventory() != e.getView().getTopInventory()) return;

        int slot = e.getSlot();
        int index = -1;
        for (int i = 0; i < SLOTS.length; i++) if (SLOTS[i] == slot) index = i;

        switch (h.kind) {
            case PICK -> {
                if (index >= 0) {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1.2f);
                    openPreview(p, Origin.values()[index]);
                }
            }
            case PREVIEW -> {
                if (slot == BACK_SLOT) {
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 0.8f);
                    openPick(p);
                } else if (slot == CONFIRM_SLOT) {
                    if (store.getOrigin(p.getUniqueId()) != null) { p.closeInventory(); return; }
                    store.setOrigin(p.getUniqueId(), h.origin);
                    p.closeInventory();
                    giveWand(p);
                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                    p.showTitle(Title.title(mm(h.origin.title()), mm("<gray>Your origin has awakened")));
                    p.sendMessage(mm("<gradient:#f6d365:#fda085>You are now " + h.origin.label()
                            + "! Use the Origin Wand (right-click to cast, left-click to switch).</gradient>"));
                }
            }
            case ABILITIES -> {
                if (index >= 0) {
                    Ability a = h.origin.abilities().get(index);
                    if (a.active()) {
                        abilities.select(p, a);
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.4f);
                        p.sendActionBar(mm("<gray>Selected: <white>" + a.label()));
                        openAbilities(p, h.origin);
                    } else {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 1f);
                    }
                } else if (slot == BOTTOM_SLOT) {
                    giveWand(p);
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof MenuHolder) e.setCancelled(true);
    }

    /** New players can't skip the origin menu: it reopens until they choose. */
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof MenuHolder h)) return;
        if (h.kind == Kind.ABILITIES) return;
        if (!(e.getPlayer() instanceof Player p)) return;
        if (store.getOrigin(p.getUniqueId()) != null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof MenuHolder) return;
            if (store.getOrigin(p.getUniqueId()) == null) openPick(p);
        }, 2L);
    }
}
