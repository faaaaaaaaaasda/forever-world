package dev.forever.origins;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Origin {
    PHOENIX("Phoenix", Material.BLAZE_POWDER, "#f12711", "#f5af19", "Reborn from flame. Fire is your friend."),
    FROSTBORN("Frostborn", Material.BLUE_ICE, "#74ebd5", "#acb6e5", "Cold, calm and unstoppable."),
    AVIAN("Avian", Material.FEATHER, "#00c6ff", "#0072ff", "Light as the wind, never afraid of a fall."),
    VERDANT("Verdant", Material.OAK_SAPLING, "#a8ff78", "#38ef7d", "Child of the forest, strong with nature."),
    UMBRA("Umbra", Material.ECHO_SHARD, "#8e2de2", "#4a00e0", "Walk in shadow and strike unseen."),
    TIDEBORN("Tideborn", Material.PRISMARINE_SHARD, "#4facfe", "#00f2fe", "The ocean answers your call."),
    STONEBORN("Stoneborn", Material.DEEPSLATE, "#bdc3c7", "#2c3e50", "Heavy, tough and impossible to move."),
    ARCANE("Arcane", Material.ENCHANTED_BOOK, "#c471f5", "#fa71cd", "Bend reality with raw magic."),
    BEASTKIN("Beastkin", Material.BONE, "#f6d365", "#fda085", "Wild instincts and a pack at your side."),
    CELESTIAL("Celestial", Material.NETHER_STAR, "#e0c3fc", "#8ec5fc", "Blessed by the stars above.");

    private final String label;
    private final Material icon;
    private final String c1, c2, description;
    private List<Ability> abilities;

    Origin(String label, Material icon, String c1, String c2, String description) {
        this.label = label;
        this.icon = icon;
        this.c1 = c1;
        this.c2 = c2;
        this.description = description;
    }

    public String label() { return label; }
    public Material icon() { return icon; }
    public String color() { return c1; }
    public String color2() { return c2; }
    public String description() { return description; }

    public String title() {
        return "<gradient:" + c1 + ":" + c2 + "><bold>" + label + "</bold></gradient>";
    }

    /** The ten abilities of this origin (5 passive first, then 5 active). */
    public List<Ability> abilities() {
        if (abilities == null) {
            List<Ability> list = new ArrayList<>();
            for (Ability a : Ability.values()) if (a.origin() == this) list.add(a);
            abilities = Collections.unmodifiableList(list);
        }
        return abilities;
    }
}
