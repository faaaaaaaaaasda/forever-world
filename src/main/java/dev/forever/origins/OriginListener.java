package dev.forever.origins;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

import static dev.forever.origins.ForeverOrigins.mm;

public class OriginListener implements Listener {
    private static final Set<EntityType> UNDEAD = Set.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,
            EntityType.DROWNED, EntityType.STRAY, EntityType.WITHER_SKELETON, EntityType.PHANTOM,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.SKELETON_HORSE, EntityType.ZOMBIE_HORSE);

    private final ForeverOrigins plugin;
    private final PlayerStore store;
    private final AbilityManager abilities;
    private final MenuManager menus;
    private boolean thorning = false;

    public OriginListener(ForeverOrigins plugin, PlayerStore store, AbilityManager abilities, MenuManager menus) {
        this.plugin = plugin;
        this.store = store;
        this.abilities = abilities;
        this.menus = menus;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            if (store.getOrigin(p.getUniqueId()) == null) {
                p.sendMessage(mm("<gradient:#f6d365:#fda085><bold>Welcome!</bold> Choose your origin to begin.</gradient>"));
                menus.openPick(p);
            } else if (plugin.getConfig().getBoolean("give-wand-on-join", true)) {
                menus.giveWand(p);
            }
        }, 20L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!menus.isWand(e.getItem())) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        Origin o = abilities.origin(p);
        if (o == null) { menus.openPick(p); return; }
        Action ac = e.getAction();
        if (ac == Action.RIGHT_CLICK_AIR || ac == Action.RIGHT_CLICK_BLOCK) {
            if (p.isSneaking()) menus.openAbilities(p, o);
            else abilities.useSelected(p);
        } else if (ac == Action.LEFT_CLICK_AIR || ac == Action.LEFT_CLICK_BLOCK) {
            abilities.cycle(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        Origin o = abilities.origin(p);
        if (o == null) return;
        for (Ability a : o.abilities()) {
            if (a.flag() != null && AbilityManager.blocks(a.flag(), e.getCause())) {
                e.setCancelled(true);
                return;
            }
        }
        if (abilities.has(p, Ability.Flag.REBIRTH)
                && p.getHealth() - e.getFinalDamage() <= 0
                && abilities.tryRebirth(p)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p && e.getEntity() instanceof LivingEntity victim) {
            if (abilities.has(p, Ability.Flag.BURN_HITS)) victim.setFireTicks(80);
            if (abilities.has(p, Ability.Flag.SLOW_HITS))
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1));
            if (abilities.has(p, Ability.Flag.LIFESTEAL)) {
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                p.setHealth(Math.min(max, p.getHealth() + e.getFinalDamage() * 0.25));
            }
        }
        if (e.getEntity() instanceof Player v && abilities.has(v, Ability.Flag.THORNS)
                && e.getDamager() instanceof LivingEntity attacker && !thorning) {
            thorning = true;
            try {
                attacker.damage(2, v);
            } finally {
                thorning = false;
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() instanceof Player p && UNDEAD.contains(e.getEntityType())
                && abilities.has(p, Ability.Flag.UNDEAD_FRIEND)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onXp(PlayerExpChangeEvent e) {
        if (abilities.has(e.getPlayer(), Ability.Flag.DOUBLE_XP)) e.setAmount(e.getAmount() * 2);
    }

    /** Fireballs from origin abilities never destroy blocks. */
    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        Entity ent = e.getEntity();
        if (ent.getPersistentDataContainer().has(plugin.fireballKey, PersistentDataType.BYTE)) {
            e.blockList().clear();
        }
    }
}
