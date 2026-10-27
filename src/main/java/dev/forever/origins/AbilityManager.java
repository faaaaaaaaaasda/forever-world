package dev.forever.origins;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

import static dev.forever.origins.ForeverOrigins.mm;

public class AbilityManager {
    private final ForeverOrigins plugin;
    private final PlayerStore store;
    private final Map<UUID, EnumMap<Ability, Long>> cooldowns = new HashMap<>();
    private final Map<UUID, Long> rebirth = new HashMap<>();
    private final Random random = new Random();
    private int tickCount = 0;

    public AbilityManager(ForeverOrigins plugin, PlayerStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    // ---------- helpers ----------

    public Origin origin(Player p) { return store.getOrigin(p.getUniqueId()); }

    public boolean has(Player p, Ability.Flag f) {
        Origin o = origin(p);
        if (o == null) return false;
        for (Ability a : o.abilities()) if (a.flag() == f) return true;
        return false;
    }

    public List<Ability> actives(Origin o) {
        List<Ability> list = new ArrayList<>();
        for (Ability a : o.abilities()) if (a.active()) list.add(a);
        return list;
    }

    public Ability selected(Player p) {
        Origin o = origin(p);
        return o == null ? null : store.getSelected(p.getUniqueId(), o);
    }

    public void select(Player p, Ability a) {
        store.setSelected(p.getUniqueId(), a);
    }

    public static boolean blocks(Ability.Flag f, DamageCause c) {
        return switch (f) {
            case FALL_IMMUNE -> c == DamageCause.FALL;
            case FREEZE_IMMUNE -> c == DamageCause.FREEZE;
            case LIGHTNING_IMMUNE -> c == DamageCause.LIGHTNING;
            case EXPLOSION_IMMUNE -> c == DamageCause.BLOCK_EXPLOSION || c == DamageCause.ENTITY_EXPLOSION;
            case MAGIC_IMMUNE -> c == DamageCause.MAGIC;
            case POISON_IMMUNE -> c == DamageCause.POISON;
            default -> false;
        };
    }

    // ---------- passives (run once a second) ----------

    public void tickPassives() {
        tickCount++;
        for (Player p : Bukkit.getOnlinePlayers()) {
            Origin o = origin(p);
            if (o == null) continue;
            for (Ability a : o.abilities()) {
                if (a.active()) continue;
                if (a.effect() != null) {
                    p.addPotionEffect(new PotionEffect(a.effect(), 220, a.amp(), true, false, false));
                } else if (a.flag() == Ability.Flag.NO_KNOCKBACK) {
                    AttributeInstance at = p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
                    if (at != null && at.getBaseValue() < 1.0) at.setBaseValue(1.0);
                } else if (a.flag() == Ability.Flag.DAYLIGHT_REGEN && tickCount % 5 == 0) {
                    World w = p.getWorld();
                    if (w.getEnvironment() == World.Environment.NORMAL && w.isDayTime()
                            && p.getLocation().getBlock().getLightFromSky() == 15) {
                        double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        if (p.getHealth() < max) p.setHealth(Math.min(max, p.getHealth() + 1));
                    }
                }
            }
        }
    }

    /** Called when a lethal hit lands on a player with a rebirth passive. */
    public boolean tryRebirth(Player p) {
        long now = System.currentTimeMillis();
        if (now < rebirth.getOrDefault(p.getUniqueId(), 0L)) return false;
        long cd = plugin.getConfig().getLong("rebirth-cooldown-seconds", 300) * 1000L;
        rebirth.put(p.getUniqueId(), now + cd);
        double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        p.setHealth(max);
        p.setFireTicks(0);
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 1));
        Location l = p.getLocation().add(0, 1, 0);
        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, l, 80, 0.5, 1, 0.5, 0.2);
        p.getWorld().spawnParticle(Particle.HEART, l, 10, 0.5, 0.5, 0.5, 0);
        p.getWorld().playSound(l, Sound.ITEM_TOTEM_USE, 1f, 1f);
        p.sendActionBar(mm("<gradient:#f6d365:#fda085><bold>You were reborn!</bold></gradient>"));
        return true;
    }

    // ---------- actives ----------

    public void cycle(Player p) {
        Origin o = origin(p);
        if (o == null) return;
        List<Ability> list = actives(o);
        Ability cur = selected(p);
        int next = (list.indexOf(cur) + 1) % list.size();
        Ability a = list.get(next);
        select(p, a);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.5f);
        p.sendActionBar(mm("<gray>Selected: <gradient:" + o.color() + ":" + o.color2() + "><bold>" + a.label() + "</bold></gradient>"));
    }

    public void useSelected(Player p) {
        Ability a = selected(p);
        if (a == null) return;
        long now = System.currentTimeMillis();
        EnumMap<Ability, Long> map = cooldowns.computeIfAbsent(p.getUniqueId(), k -> new EnumMap<>(Ability.class));
        long ready = map.getOrDefault(a, 0L);
        if (now < ready) {
            p.sendActionBar(mm("<red>" + a.label() + " is recharging... <white>"
                    + String.format("%.1f", (ready - now) / 1000.0) + "s"));
            return;
        }
        execute(p, a);
        map.put(a, now + (long) (a.cooldown() * 1000));
        Origin o = a.origin();
        p.sendActionBar(mm("<gradient:" + o.color() + ":" + o.color2() + "><bold>" + a.label() + "</bold></gradient>"));
    }

    private void execute(Player p, Ability a) {
        final World w = p.getWorld();
        final Location loc = p.getLocation();
        final double pw = a.power();
        final Particle pt = a.particle();

        switch (a.act()) {
            case DASH -> {
                Vector d = loc.getDirection().clone();
                d.setY(Math.max(0.1, d.getY() * 0.4));
                d.normalize().multiply(pw);
                p.setVelocity(d);
                w.spawnParticle(pt, loc.clone().add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0.05);
                w.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.4f);
            }
            case LEAP -> {
                Vector v = p.getVelocity();
                v.setY(pw);
                p.setVelocity(v);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 120, 0));
                ring(loc.clone().add(0, 0.2, 0), 1.2, pt, 20);
                w.playSound(loc, Sound.ENTITY_RABBIT_JUMP, 1f, 0.8f);
            }
            case HEAL -> {
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                p.setHealth(Math.min(max, p.getHealth() + pw));
                p.setFoodLevel(Math.min(20, p.getFoodLevel() + 4));
                w.spawnParticle(pt, loc.clone().add(0, 2, 0), 12, 0.5, 0.3, 0.5, 0);
                w.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            }
            case FIREBALL -> {
                Fireball fb = p.launchProjectile(Fireball.class, loc.getDirection());
                fb.setYield((float) pw);
                fb.setIsIncendiary(false);
                fb.getPersistentDataContainer().set(plugin.fireballKey, PersistentDataType.BYTE, (byte) 1);
                w.playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 0.8f);
            }
            case ARROW_SHOT -> {
                Arrow ar = p.launchProjectile(Arrow.class, loc.getDirection().multiply(pw));
                ar.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                ar.setCritical(true);
                ar.setDamage(5);
                w.spawnParticle(pt, p.getEyeLocation(), 15, 0.2, 0.2, 0.2, 0.05);
                w.playSound(loc, Sound.ENTITY_ARROW_SHOOT, 1f, 1.2f);
            }
            case VOLLEY -> {
                int n = (int) pw;
                for (int i = 0; i < n; i++) {
                    Vector d = loc.getDirection().clone().add(new Vector(
                            (random.nextDouble() - 0.5) * 0.3,
                            (random.nextDouble() - 0.5) * 0.15,
                            (random.nextDouble() - 0.5) * 0.3)).normalize().multiply(2.5);
                    Arrow ar = p.launchProjectile(Arrow.class, d);
                    ar.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    ar.setDamage(3);
                }
                w.spawnParticle(pt, p.getEyeLocation(), 20, 0.4, 0.3, 0.4, 0.05);
                w.playSound(loc, Sound.ENTITY_ARROW_SHOOT, 1f, 0.8f);
            }
            case LIGHTNING -> {
                Block b = p.getTargetBlockExact(40);
                final Location target = b != null
                        ? b.getLocation().add(0.5, 1, 0.5)
                        : p.getEyeLocation().add(loc.getDirection().multiply(20));
                final int bolts = (int) pw;
                new BukkitRunnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        if (i++ >= bolts || !p.isOnline()) { cancel(); return; }
                        Location s = target.clone().add((random.nextDouble() - 0.5) * 4, 0, (random.nextDouble() - 0.5) * 4);
                        w.strikeLightningEffect(s);
                        w.spawnParticle(pt, s, 25, 0.5, 0.5, 0.5, 0.1);
                        for (LivingEntity le : targets(p, s, 3)) le.damage(8, p);
                    }
                }.runTaskTimer(plugin, 0L, 5L);
            }
            case AOE_DEBUFF -> {
                for (LivingEntity le : targets(p, loc, pw)) {
                    if (a.effect() != null)
                        le.addPotionEffect(new PotionEffect(a.effect(), a.secs() * 20, a.amp()));
                    if (pt == Particle.SNOWFLAKE) le.setFreezeTicks(100 + a.secs() * 20);
                }
                ring(loc.clone().add(0, 0.2, 0), pw, pt, 70);
                ring(loc.clone().add(0, 0.6, 0), pw / 2, pt, 40);
                w.playSound(loc, Sound.BLOCK_GLASS_BREAK, 1f, 0.7f);
            }
            case IGNITE_AOE -> {
                for (LivingEntity le : targets(p, loc, pw)) le.setFireTicks(a.secs() * 20);
                ring(loc.clone().add(0, 0.2, 0), pw, pt, 60);
                w.playSound(loc, Sound.ITEM_FIRECHARGE_USE, 1f, 0.8f);
            }
            case SHOCKWAVE -> {
                for (LivingEntity le : targets(p, loc, pw)) {
                    Vector kb = le.getLocation().toVector().subtract(loc.toVector()).setY(0);
                    if (kb.lengthSquared() < 0.01) kb = loc.getDirection().setY(0);
                    kb.normalize().multiply(1.4).setY(0.5);
                    le.setVelocity(kb);
                    le.damage(2 + pw * 0.5, p);
                }
                w.spawnParticle(Particle.EXPLOSION_LARGE, loc.clone().add(0, 0.5, 0), 3, 0.5, 0.2, 0.5, 0);
                ring(loc.clone().add(0, 0.2, 0), pw, pt, 60);
                w.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.2f);
            }
            case MAGNET -> {
                final double r = pw;
                ring(loc.clone().add(0, 0.5, 0), r, pt, 80);
                w.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 1.6f);
                new BukkitRunnable() {
                    int n = 0;
                    @Override
                    public void run() {
                        if (n++ >= 20 || !p.isOnline()) { cancel(); return; }
                        Location to = p.getLocation().add(0, 0.5, 0);
                        for (Entity e : p.getNearbyEntities(r, r, r)) {
                            if (e instanceof Item || e instanceof ExperienceOrb) {
                                Vector pull = to.toVector().subtract(e.getLocation().toVector());
                                if (pull.lengthSquared() > 0.25) e.setVelocity(pull.normalize().multiply(1.0));
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0L, 3L);
            }
            case GRAVITY -> {
                final double r = pw;
                ring(loc.clone().add(0, 0.5, 0), r, pt, 80);
                w.playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.6f);
                new BukkitRunnable() {
                    int n = 0;
                    @Override
                    public void run() {
                        if (n++ >= 10 || !p.isOnline()) { cancel(); return; }
                        Location to = p.getLocation();
                        for (LivingEntity le : targets(p, to, r)) {
                            Vector pull = to.toVector().subtract(le.getLocation().toVector());
                            if (pull.lengthSquared() > 4) le.setVelocity(pull.normalize().multiply(0.7));
                        }
                        ring(to.clone().add(0, 0.5, 0), 2.5, pt, 20);
                    }
                }.runTaskTimer(plugin, 0L, 4L);
            }
            case BUFF -> {
                p.addPotionEffect(new PotionEffect(a.effect(), a.secs() * 20, a.amp()));
                ring(loc.clone().add(0, 0.2, 0), 1.5, pt, 30);
                ring(loc.clone().add(0, 1.2, 0), 1.0, pt, 20);
                w.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1.2f);
            }
            case BLINK -> {
                Location eye = p.getEyeLocation();
                Vector dir = eye.getDirection();
                RayTraceResult hit = w.rayTraceBlocks(eye, dir, pw, FluidCollisionMode.NEVER, true);
                Vector end = (hit != null && hit.getHitPosition() != null)
                        ? hit.getHitPosition().subtract(dir.clone().multiply(0.7))
                        : eye.toVector().add(dir.clone().multiply(pw));
                Location dest = end.toLocation(w, loc.getYaw(), loc.getPitch()).subtract(0, 1.6, 0);
                if (!dest.getBlock().isPassable()) dest.add(0, 1, 0);
                w.spawnParticle(pt, loc.clone().add(0, 1, 0), 40, 0.3, 0.6, 0.3, 0.5);
                p.teleport(dest);
                p.setFallDistance(0);
                w.spawnParticle(pt, dest.clone().add(0, 1, 0), 40, 0.3, 0.6, 0.3, 0.5);
                w.playSound(dest, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.2f);
            }
            case GROW -> {
                int r = (int) pw;
                int count = 0;
                for (int x = -r; x <= r && count < 40; x++)
                    for (int y = -2; y <= 2 && count < 40; y++)
                        for (int z = -r; z <= r && count < 40; z++) {
                            Block b = loc.getBlock().getRelative(x, y, z);
                            if (b.applyBoneMeal(BlockFace.UP)) count++;
                        }
                ring(loc.clone().add(0, 0.5, 0), r, pt, 50);
                w.playSound(loc, Sound.ITEM_BONE_MEAL_USE, 1f, 1f);
            }
            case SUMMON_WOLVES -> {
                final List<Wolf> wolves = new ArrayList<>();
                for (int i = 0; i < (int) pw; i++) {
                    Wolf wolf = w.spawn(loc.clone().add(random.nextDouble() * 2 - 1, 0, random.nextDouble() * 2 - 1), Wolf.class);
                    wolf.setTamed(true);
                    wolf.setOwner(p);
                    wolf.setAdult();
                    wolf.setPersistent(false);
                    wolves.add(wolf);
                }
                w.spawnParticle(pt, loc.clone().add(0, 1.5, 0), 15, 0.8, 0.3, 0.8, 0);
                w.playSound(loc, Sound.ENTITY_WOLF_HOWL, 1f, 1f);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Wolf wolf : wolves) {
                        if (wolf.isValid()) {
                            wolf.getWorld().spawnParticle(Particle.CLOUD, wolf.getLocation(), 10, 0.3, 0.3, 0.3, 0.02);
                            wolf.remove();
                        }
                    }
                }, a.secs() * 20L);
            }
        }
    }

    private List<LivingEntity> targets(Player p, Location center, double r) {
        boolean pvp = plugin.getConfig().getBoolean("affect-players", false);
        List<LivingEntity> list = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, r, r, r)) {
            if (e.equals(p) || e instanceof ArmorStand) continue;
            if (e instanceof Tameable t && p.equals(t.getOwner())) continue;
            if (e instanceof LivingEntity le && (pvp || !(e instanceof Player))) list.add(le);
        }
        return list;
    }

    private void ring(Location c, double r, Particle particle, int points) {
        World w = c.getWorld();
        for (int i = 0; i < points; i++) {
            double ang = 2 * Math.PI * i / points;
            w.spawnParticle(particle, c.getX() + Math.cos(ang) * r, c.getY(), c.getZ() + Math.sin(ang) * r, 1, 0, 0, 0, 0);
        }
    }
}
