package dev.forever.origins;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/** Saves each player's origin and selected active ability to data.yml. */
public class PlayerStore {
    private final ForeverOrigins plugin;
    private final File file;
    private final YamlConfiguration yml;

    public PlayerStore(ForeverOrigins plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdirs();
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.yml = YamlConfiguration.loadConfiguration(file);
    }

    public Origin getOrigin(UUID id) {
        String s = yml.getString("players." + id + ".origin");
        if (s == null) return null;
        try {
            return Origin.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOrigin(UUID id, Origin origin) {
        yml.set("players." + id + ".origin", origin.name());
        yml.set("players." + id + ".selected", null);
        save();
    }

    public void clearOrigin(UUID id) {
        yml.set("players." + id, null);
        save();
    }

    public Ability getSelected(UUID id, Origin origin) {
        String s = yml.getString("players." + id + ".selected");
        if (s != null) {
            try {
                Ability a = Ability.valueOf(s);
                if (a.origin() == origin && a.active()) return a;
            } catch (IllegalArgumentException ignored) { }
        }
        for (Ability a : origin.abilities()) if (a.active()) return a;
        return null;
    }

    public void setSelected(UUID id, Ability ability) {
        yml.set("players." + id + ".selected", ability.name());
        save();
    }

    public void save() {
        try {
            yml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data.yml: " + e.getMessage());
        }
    }
}
