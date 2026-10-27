package dev.forever.origins;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffectType;

/** All 100 abilities: 10 origins x (5 passive + 5 active). */
public enum Ability {

    // ===================== PHOENIX =====================
    PHOENIX_FIREPROOF(Origin.PHOENIX, "Fireproof", Material.MAGMA_CREAM, "Immune to fire and lava.", PotionEffectType.FIRE_RESISTANCE, 0),
    PHOENIX_HOT_BLOODED(Origin.PHOENIX, "Hot Blooded", Material.BLAZE_ROD, "Your melee hits set enemies ablaze.", Flag.BURN_HITS),
    PHOENIX_REBIRTH(Origin.PHOENIX, "Ashen Rebirth", Material.TOTEM_OF_UNDYING, "Survive a deadly hit (5 min recharge).", Flag.REBIRTH),
    PHOENIX_EMBER_SKIN(Origin.PHOENIX, "Ember Skin", Material.NETHERITE_SCRAP, "Take less damage from everything.", PotionEffectType.DAMAGE_RESISTANCE, 0),
    PHOENIX_FLAME_STEP(Origin.PHOENIX, "Flame Step", Material.GOLDEN_BOOTS, "Move faster.", PotionEffectType.SPEED, 0),
    PHOENIX_FIREBALL(Origin.PHOENIX, "Fireball", Material.FIRE_CHARGE, "Hurl a fireball that never breaks blocks.", 7, Act.FIREBALL, 1.5, Particle.FLAME),
    PHOENIX_BURST(Origin.PHOENIX, "Flame Burst", Material.CAMPFIRE, "Ignite every mob around you.", 12, Act.IGNITE_AOE, 6, Particle.FLAME, null, 0, 6),
    PHOENIX_DASH(Origin.PHOENIX, "Fire Dash", Material.BLAZE_POWDER, "Rocket forward in a streak of flame.", 6, Act.DASH, 1.8, Particle.FLAME),
    PHOENIX_NOVA(Origin.PHOENIX, "Inferno Nova", Material.MAGMA_BLOCK, "Blast enemies away in a ring of fire.", 15, Act.SHOCKWAVE, 8, Particle.FLAME),
    PHOENIX_FLIGHT(Origin.PHOENIX, "Phoenix Flight", Material.ORANGE_DYE, "Soar into the sky and glide down.", 14, Act.LEAP, 1.6, Particle.FLAME),

    // ===================== FROSTBORN =====================
    FROST_SKIN(Origin.FROSTBORN, "Frost Skin", Material.POWDER_SNOW_BUCKET, "Immune to freezing.", Flag.FREEZE_IMMUNE),
    FROST_WALKER(Origin.FROSTBORN, "Snow Walker", Material.LEATHER_BOOTS, "Move faster.", PotionEffectType.SPEED, 0),
    FROST_VEINS(Origin.FROSTBORN, "Icy Veins", Material.PACKED_ICE, "Take less damage.", PotionEffectType.DAMAGE_RESISTANCE, 0),
    FROST_HEART(Origin.FROSTBORN, "Ice Heart", Material.HEART_OF_THE_SEA, "Extra absorption hearts.", PotionEffectType.ABSORPTION, 1),
    FROST_CHILL(Origin.FROSTBORN, "Chill Touch", Material.ICE, "Melee hits slow enemies.", Flag.SLOW_HITS),
    FROST_NOVA(Origin.FROSTBORN, "Frost Nova", Material.SNOW_BLOCK, "Freeze everything nearby.", 15, Act.AOE_DEBUFF, 8, Particle.SNOWFLAKE, PotionEffectType.SLOW, 2, 6),
    FROST_SHARD(Origin.FROSTBORN, "Ice Shard", Material.ARROW, "Fire a fast shard of ice.", 4, Act.ARROW_SHOT, 3.0, Particle.SNOWFLAKE),
    FROST_BLIZZARD(Origin.FROSTBORN, "Blizzard", Material.SNOWBALL, "Weaken enemies in a wide storm.", 20, Act.AOE_DEBUFF, 12, Particle.SNOWFLAKE, PotionEffectType.WEAKNESS, 1, 8),
    FROST_SLIDE(Origin.FROSTBORN, "Ice Slide", Material.BLUE_ICE, "Slide forward on a cold wind.", 6, Act.DASH, 1.6, Particle.SNOWFLAKE),
    FROST_ZERO(Origin.FROSTBORN, "Absolute Zero", Material.PRISMARINE_CRYSTALS, "Lock nearby enemies in solid ice.", 30, Act.AOE_DEBUFF, 7, Particle.SNOWFLAKE, PotionEffectType.SLOW, 6, 5),

    // ===================== AVIAN =====================
    AVIAN_FEATHERFALL(Origin.AVIAN, "Featherfall", Material.FEATHER, "No fall damage.", Flag.FALL_IMMUNE),
    AVIAN_SWIFT(Origin.AVIAN, "Swift Wings", Material.ELYTRA, "Move faster.", PotionEffectType.SPEED, 0),
    AVIAN_HAWK(Origin.AVIAN, "Hawk Eye", Material.SPYGLASS, "See in the dark.", PotionEffectType.NIGHT_VISION, 0),
    AVIAN_BONES(Origin.AVIAN, "Hollow Bones", Material.BONE_MEAL, "Jump higher.", PotionEffectType.JUMP, 1),
    AVIAN_TREASURE(Origin.AVIAN, "Treasure Eye", Material.GOLD_NUGGET, "Better luck on loot and fishing.", PotionEffectType.LUCK, 1),
    AVIAN_DASH(Origin.AVIAN, "Wing Dash", Material.PHANTOM_MEMBRANE, "Burst forward on the wind.", 6, Act.DASH, 1.8, Particle.CLOUD),
    AVIAN_LEAP(Origin.AVIAN, "Sky Leap", Material.RABBIT_FOOT, "Spring high into the sky.", 8, Act.LEAP, 1.2, Particle.CLOUD),
    AVIAN_GUST(Origin.AVIAN, "Wind Gust", Material.WHITE_BANNER, "Blast enemies away with a gust.", 10, Act.SHOCKWAVE, 7, Particle.CLOUD),
    AVIAN_VOLLEY(Origin.AVIAN, "Feather Volley", Material.SPECTRAL_ARROW, "Loose a fan of arrows.", 12, Act.VOLLEY, 7, Particle.CRIT),
    AVIAN_SOAR(Origin.AVIAN, "Soar", Material.FIREWORK_ROCKET, "Rocket far into the air.", 18, Act.LEAP, 2.2, Particle.FIREWORKS_SPARK),

    // ===================== VERDANT =====================
    VERDANT_PHOTO(Origin.VERDANT, "Photosynthesis", Material.SUNFLOWER, "Slowly heal in daylight.", Flag.DAYLIGHT_REGEN),
    VERDANT_THORNS(Origin.VERDANT, "Thorny Hide", Material.SWEET_BERRIES, "Reflect damage onto attackers.", Flag.THORNS),
    VERDANT_OAK(Origin.VERDANT, "Oak Strength", Material.OAK_LOG, "Hit harder.", PotionEffectType.INCREASE_DAMAGE, 0),
    VERDANT_PURE(Origin.VERDANT, "Pure Blood", Material.SPIDER_EYE, "Immune to poison damage.", Flag.POISON_IMMUNE),
    VERDANT_WILD(Origin.VERDANT, "Wild Speed", Material.SUGAR_CANE, "Move faster.", PotionEffectType.SPEED, 0),
    VERDANT_BLOOM(Origin.VERDANT, "Bloom", Material.BONE_MEAL, "Bonemeal plants and crops around you.", 10, Act.GROW, 6, Particle.VILLAGER_HAPPY),
    VERDANT_ENTANGLE(Origin.VERDANT, "Entangle", Material.VINE, "Root nearby enemies in place.", 14, Act.AOE_DEBUFF, 8, Particle.VILLAGER_HAPPY, PotionEffectType.SLOW, 5, 4),
    VERDANT_HEAL(Origin.VERDANT, "Verdant Heal", Material.GLISTERING_MELON_SLICE, "Restore health.", 20, Act.HEAL, 8, Particle.HEART),
    VERDANT_VOLLEY(Origin.VERDANT, "Thorn Volley", Material.TIPPED_ARROW, "Fire a spray of thorns.", 12, Act.VOLLEY, 6, Particle.CRIT),
    VERDANT_OVERGROWTH(Origin.VERDANT, "Overgrowth", Material.MOSS_BLOCK, "Gain powerful regeneration.", 25, Act.BUFF, 0, Particle.VILLAGER_HAPPY, PotionEffectType.REGENERATION, 2, 8),

    // ===================== UMBRA =====================
    UMBRA_NIGHT(Origin.UMBRA, "Night Sight", Material.ENDER_EYE, "See in the dark.", PotionEffectType.NIGHT_VISION, 0),
    UMBRA_SHADE(Origin.UMBRA, "Shade Born", Material.WITHER_SKELETON_SKULL, "Undead mobs leave you alone.", Flag.UNDEAD_FRIEND),
    UMBRA_SPEED(Origin.UMBRA, "Dark Speed", Material.BLACK_DYE, "Move faster.", PotionEffectType.SPEED, 0),
    UMBRA_DRAIN(Origin.UMBRA, "Soul Drain", Material.SOUL_LANTERN, "Heal when you hit enemies.", Flag.LIFESTEAL),
    UMBRA_RESIL(Origin.UMBRA, "Dark Resilience", Material.OBSIDIAN, "Extra absorption hearts.", PotionEffectType.ABSORPTION, 1),
    UMBRA_BLINK(Origin.UMBRA, "Shadow Blink", Material.ENDER_PEARL, "Teleport to where you look.", 5, Act.BLINK, 20, Particle.PORTAL),
    UMBRA_DASH(Origin.UMBRA, "Shadow Dash", Material.NETHERITE_BOOTS, "Slip forward in a blur.", 6, Act.DASH, 1.8, Particle.SMOKE_LARGE),
    UMBRA_BLACKOUT(Origin.UMBRA, "Blackout", Material.COAL_BLOCK, "Blind everything around you.", 16, Act.AOE_DEBUFF, 8, Particle.SMOKE_LARGE, PotionEffectType.BLINDNESS, 0, 6),
    UMBRA_CLOAK(Origin.UMBRA, "Cloak of Dusk", Material.BLACK_BANNER, "Turn invisible for a while.", 20, Act.BUFF, 0, Particle.SMOKE_NORMAL, PotionEffectType.INVISIBILITY, 0, 10),
    UMBRA_REND(Origin.UMBRA, "Soul Rend", Material.SCULK_CATALYST, "Rip through enemies with a soul shockwave.", 14, Act.SHOCKWAVE, 7, Particle.SOUL_FIRE_FLAME),

    // ===================== TIDEBORN =====================
    TIDE_GILLS(Origin.TIDEBORN, "Gills", Material.NAUTILUS_SHELL, "Breathe underwater.", PotionEffectType.WATER_BREATHING, 0),
    TIDE_DOLPHIN(Origin.TIDEBORN, "Dolphin Grace", Material.TROPICAL_FISH, "Swim fast.", PotionEffectType.DOLPHINS_GRACE, 1),
    TIDE_CONDUIT(Origin.TIDEBORN, "Conduit Power", Material.CONDUIT, "Underwater sight and mining.", PotionEffectType.CONDUIT_POWER, 0),
    TIDE_DEEP(Origin.TIDEBORN, "Deep Heart", Material.SEA_LANTERN, "Extra max health.", PotionEffectType.HEALTH_BOOST, 0),
    TIDE_STORMBORN(Origin.TIDEBORN, "Storm Born", Material.LIGHTNING_ROD, "Immune to lightning.", Flag.LIGHTNING_IMMUNE),
    TIDE_WAVE(Origin.TIDEBORN, "Tidal Wave", Material.WATER_BUCKET, "Smash enemies away in a wave.", 10, Act.SHOCKWAVE, 8, Particle.WATER_SPLASH),
    TIDE_WHIRL(Origin.TIDEBORN, "Whirlpool", Material.KELP, "Pull items and XP to you.", 10, Act.MAGNET, 12, Particle.WATER_BUBBLE),
    TIDE_STORM(Origin.TIDEBORN, "Storm Call", Material.TRIDENT, "Call lightning on your target.", 12, Act.LIGHTNING, 3, Particle.ELECTRIC_SPARK),
    TIDE_HEAL(Origin.TIDEBORN, "Ocean Heal", Material.SEA_PICKLE, "Restore health.", 20, Act.HEAL, 8, Particle.WATER_BUBBLE),
    TIDE_RIPTIDE(Origin.TIDEBORN, "Riptide Leap", Material.COD, "Launch up from the waves.", 8, Act.LEAP, 1.3, Particle.WATER_SPLASH),

    // ===================== STONEBORN =====================
    STONE_IRON(Origin.STONEBORN, "Iron Skin", Material.IRON_CHESTPLATE, "Take much less damage.", PotionEffectType.DAMAGE_RESISTANCE, 1),
    STONE_MINER(Origin.STONEBORN, "Miner's Might", Material.IRON_PICKAXE, "Mine faster.", PotionEffectType.FAST_DIGGING, 1),
    STONE_UNMOVABLE(Origin.STONEBORN, "Unmovable", Material.ANVIL, "Immune to knockback.", Flag.NO_KNOCKBACK),
    STONE_BLASTPROOF(Origin.STONEBORN, "Blast Proof", Material.TNT, "Immune to explosions.", Flag.EXPLOSION_IMMUNE),
    STONE_HEART(Origin.STONEBORN, "Tough Heart", Material.RAW_IRON, "Lots of extra max health.", PotionEffectType.HEALTH_BOOST, 1),
    STONE_QUAKE(Origin.STONEBORN, "Earthquake", Material.COARSE_DIRT, "Shake the ground and shove enemies back.", 10, Act.SHOCKWAVE, 7, Particle.EXPLOSION_NORMAL),
    STONE_SKIN(Origin.STONEBORN, "Stone Skin", Material.STONE, "Become nearly unbreakable briefly.", 20, Act.BUFF, 0, Particle.EXPLOSION_NORMAL, PotionEffectType.DAMAGE_RESISTANCE, 3, 8),
    STONE_TOSS(Origin.STONEBORN, "Rock Throw", Material.COBBLESTONE, "Hurl a heavy projectile.", 5, Act.ARROW_SHOT, 3.5, Particle.CRIT),
    STONE_GRAVITY(Origin.STONEBORN, "Gravity Well", Material.CRYING_OBSIDIAN, "Drag nearby mobs toward you.", 14, Act.GRAVITY, 10, Particle.PORTAL),
    STONE_TITAN(Origin.STONEBORN, "Titan Form", Material.NETHERITE_BLOCK, "Massive strength for a short time.", 30, Act.BUFF, 0, Particle.EXPLOSION_LARGE, PotionEffectType.INCREASE_DAMAGE, 2, 10),

    // ===================== ARCANE =====================
    ARCANE_MIND(Origin.ARCANE, "Arcane Mind", Material.EXPERIENCE_BOTTLE, "Gain double XP.", Flag.DOUBLE_XP),
    ARCANE_WARD(Origin.ARCANE, "Spell Ward", Material.SHIELD, "Immune to magic damage.", Flag.MAGIC_IMMUNE),
    ARCANE_SPEED(Origin.ARCANE, "Mystic Speed", Material.AMETHYST_SHARD, "Move faster.", PotionEffectType.SPEED, 0),
    ARCANE_FORTUNE(Origin.ARCANE, "Fortune's Favor", Material.LAPIS_LAZULI, "Better luck.", PotionEffectType.LUCK, 1),
    ARCANE_SHIELD(Origin.ARCANE, "Arcane Shield", Material.AMETHYST_CLUSTER, "Lots of absorption hearts.", PotionEffectType.ABSORPTION, 2),
    ARCANE_BOLT(Origin.ARCANE, "Arcane Bolt", Material.END_ROD, "Fire a bolt of pure magic.", 3, Act.ARROW_SHOT, 3.5, Particle.CRIT_MAGIC),
    ARCANE_TELEPORT(Origin.ARCANE, "Teleport", Material.CHORUS_FRUIT, "Teleport to where you look.", 5, Act.BLINK, 25, Particle.PORTAL),
    ARCANE_BURST(Origin.ARCANE, "Mana Burst", Material.GLOWSTONE_DUST, "Blast enemies away with raw mana.", 10, Act.SHOCKWAVE, 7, Particle.CRIT_MAGIC),
    ARCANE_LEVITATE(Origin.ARCANE, "Levitate", Material.SHULKER_SHELL, "Float upward for a moment.", 15, Act.BUFF, 0, Particle.END_ROD, PotionEffectType.LEVITATION, 0, 3),
    ARCANE_TIME(Origin.ARCANE, "Time Warp", Material.CLOCK, "Move at blinding speed.", 25, Act.BUFF, 0, Particle.END_ROD, PotionEffectType.SPEED, 3, 8),

    // ===================== BEASTKIN =====================
    BEAST_PAWS(Origin.BEASTKIN, "Swift Paws", Material.LEATHER_HORSE_ARMOR, "Move faster.", PotionEffectType.SPEED, 1),
    BEAST_REFLEX(Origin.BEASTKIN, "Cat Reflexes", Material.STRING, "No fall damage.", Flag.FALL_IMMUNE),
    BEAST_HIDE(Origin.BEASTKIN, "Thick Hide", Material.LEATHER, "Take less damage.", PotionEffectType.DAMAGE_RESISTANCE, 0),
    BEAST_STRENGTH(Origin.BEASTKIN, "Feral Strength", Material.BONE, "Hit harder.", PotionEffectType.INCREASE_DAMAGE, 0),
    BEAST_BLOOD(Origin.BEASTKIN, "Bloodthirst", Material.BEEF, "Heal when you hit enemies.", Flag.LIFESTEAL),
    BEAST_POUNCE(Origin.BEASTKIN, "Pounce", Material.RABBIT_HIDE, "Lunge forward.", 5, Act.DASH, 1.9, Particle.CRIT),
    BEAST_HOWL(Origin.BEASTKIN, "Terrifying Howl", Material.GOAT_HORN, "Weaken everything around you.", 18, Act.AOE_DEBUFF, 10, Particle.NOTE, PotionEffectType.WEAKNESS, 1, 8),
    BEAST_ROAR(Origin.BEASTKIN, "Wild Roar", Material.IRON_AXE, "Knock enemies back with a roar.", 10, Act.SHOCKWAVE, 7, Particle.EXPLOSION_NORMAL),
    BEAST_PACK(Origin.BEASTKIN, "Pack Call", Material.WOLF_SPAWN_EGG, "Summon three loyal wolves for 30s.", 45, Act.SUMMON_WOLVES, 3, Particle.HEART, null, 0, 30),
    BEAST_FRENZY(Origin.BEASTKIN, "Frenzy", Material.RED_DYE, "Move with wild speed.", 25, Act.BUFF, 0, Particle.CRIT, PotionEffectType.SPEED, 2, 8),

    // ===================== CELESTIAL =====================
    CELEST_HEART(Origin.CELESTIAL, "Starlight Heart", Material.NETHER_STAR, "Extra max health.", PotionEffectType.HEALTH_BOOST, 1),
    CELEST_SWIFT(Origin.CELESTIAL, "Holy Speed", Material.SUGAR, "Move faster.", PotionEffectType.SPEED, 0),
    CELEST_BLESS(Origin.CELESTIAL, "Blessed Strikes", Material.GOLDEN_SWORD, "Hit harder.", PotionEffectType.INCREASE_DAMAGE, 0),
    CELEST_ANGEL(Origin.CELESTIAL, "Angel's Grace", Material.GHAST_TEAR, "No fall damage.", Flag.FALL_IMMUNE),
    CELEST_CHANCE(Origin.CELESTIAL, "Second Chance", Material.GOLDEN_APPLE, "Survive a deadly hit (5 min recharge).", Flag.REBIRTH),
    CELEST_HEAL(Origin.CELESTIAL, "Healing Light", Material.HONEY_BOTTLE, "Restore a lot of health.", 15, Act.HEAL, 10, Particle.HEART),
    CELEST_SMITE(Origin.CELESTIAL, "Smite", Material.GOLD_BLOCK, "Call holy lightning down.", 12, Act.LIGHTNING, 4, Particle.ELECTRIC_SPARK),
    CELEST_NOVA(Origin.CELESTIAL, "Holy Nova", Material.GLOWSTONE, "Blast enemies away with radiant light.", 12, Act.SHOCKWAVE, 8, Particle.END_ROD),
    CELEST_BLESSING(Origin.CELESTIAL, "Blessing", Material.BEACON, "Gain a huge shield of absorption.", 30, Act.BUFF, 0, Particle.END_ROD, PotionEffectType.ABSORPTION, 3, 20),
    CELEST_ASCEND(Origin.CELESTIAL, "Ascend", Material.FIREWORK_STAR, "Rise high and drift down gently.", 15, Act.LEAP, 1.8, Particle.END_ROD);

    /** Special passive behaviours handled by listeners. */
    public enum Flag {
        BURN_HITS, SLOW_HITS, LIFESTEAL, THORNS, REBIRTH, UNDEAD_FRIEND, DOUBLE_XP, DAYLIGHT_REGEN, NO_KNOCKBACK,
        FALL_IMMUNE, FREEZE_IMMUNE, LIGHTNING_IMMUNE, EXPLOSION_IMMUNE, MAGIC_IMMUNE, POISON_IMMUNE
    }

    /** Active ability behaviours. */
    public enum Act {
        DASH, LEAP, HEAL, FIREBALL, ARROW_SHOT, VOLLEY, LIGHTNING, AOE_DEBUFF, IGNITE_AOE,
        SHOCKWAVE, MAGNET, GRAVITY, BUFF, BLINK, GROW, SUMMON_WOLVES
    }

    private final Origin origin;
    private final String label;
    private final Material icon;
    private final String description;
    private final PotionEffectType effect;
    private final int amp;
    private final Flag flag;
    private final Act act;
    private final double cooldown;
    private final double power;
    private final Particle particle;
    private final int secs;

    private Ability(Origin origin, String label, Material icon, String description,
                    PotionEffectType effect, int amp, Flag flag, Act act,
                    double cooldown, double power, Particle particle, int secs) {
        this.origin = origin;
        this.label = label;
        this.icon = icon;
        this.description = description;
        this.effect = effect;
        this.amp = amp;
        this.flag = flag;
        this.act = act;
        this.cooldown = cooldown;
        this.power = power;
        this.particle = particle;
        this.secs = secs;
    }

    /** Passive: potion effect. */
    Ability(Origin o, String label, Material icon, String desc, PotionEffectType effect, int amp) {
        this(o, label, icon, desc, effect, amp, null, null, 0, 0, null, 0);
    }

    /** Passive: special flag. */
    Ability(Origin o, String label, Material icon, String desc, Flag flag) {
        this(o, label, icon, desc, null, 0, flag, null, 0, 0, null, 0);
    }

    /** Active. */
    Ability(Origin o, String label, Material icon, String desc, double cooldown, Act act, double power, Particle particle) {
        this(o, label, icon, desc, null, 0, null, act, cooldown, power, particle, 0);
    }

    /** Active with an effect and duration. */
    Ability(Origin o, String label, Material icon, String desc, double cooldown, Act act, double power,
            Particle particle, PotionEffectType effect, int amp, int secs) {
        this(o, label, icon, desc, effect, amp, null, act, cooldown, power, particle, secs);
    }

    public Origin origin() { return origin; }
    public String label() { return label; }
    public Material icon() { return icon; }
    public String description() { return description; }
    public PotionEffectType effect() { return effect; }
    public int amp() { return amp; }
    public Flag flag() { return flag; }
    public Act act() { return act; }
    public double cooldown() { return cooldown; }
    public double power() { return power; }
    public Particle particle() { return particle; }
    public int secs() { return secs; }
    public boolean active() { return act != null; }
}
