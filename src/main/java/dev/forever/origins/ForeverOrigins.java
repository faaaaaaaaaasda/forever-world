package dev.forever.origins;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ForeverOrigins extends JavaPlugin implements CommandExecutor, TabCompleter {
    public NamespacedKey wandKey;
    public NamespacedKey fireballKey;

    private PlayerStore store;
    private AbilityManager abilities;
    private MenuManager menus;

    public static Component mm(String s) {
        return MiniMessage.miniMessage().deserialize(s).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        wandKey = new NamespacedKey(this, "origin_wand");
        fireballKey = new NamespacedKey(this, "origin_fireball");

        store = new PlayerStore(this);
        abilities = new AbilityManager(this, store);
        menus = new MenuManager(this, store, abilities);

        getServer().getPluginManager().registerEvents(menus, this);
        getServer().getPluginManager().registerEvents(new OriginListener(this, store, abilities, menus), this);
        getServer().getScheduler().runTaskTimer(this, abilities::tickPassives, 40L, 20L);

        var cmd = getCommand("origin");
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
        getLogger().info("ForeverOrigins enabled: " + Origin.values().length + " origins, "
                + Ability.values().length + " abilities.");
    }

    @Override
    public void onDisable() {
        if (store != null) store.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("set"))) {
            return admin(sender, args);
        }
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (!p.hasPermission("foreverorigins.use")) {
            p.sendMessage(mm("<red>You don't have permission."));
            return true;
        }
        Origin o = abilities.origin(p);
        if (o == null) {
            menus.openPick(p);
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("wand")) {
            menus.giveWand(p);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("use")) {
            abilities.useSelected(p);
        } else {
            menus.openAbilities(p, o);
        }
        return true;
    }

    private boolean admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("foreverorigins.admin")) {
            sender.sendMessage(mm("<red>You don't have permission."));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(mm("<red>Usage: /origin reset <player> | /origin set <player> <origin>"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(mm("<red>Player not found (must be online)."));
            return true;
        }
        AttributeInstance kb = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (kb != null) kb.setBaseValue(0.0);

        if (args[0].equalsIgnoreCase("reset")) {
            store.clearOrigin(target.getUniqueId());
            sender.sendMessage(mm("<green>Reset " + target.getName() + "'s origin."));
            menus.openPick(target);
        } else {
            if (args.length < 3) {
                sender.sendMessage(mm("<red>Usage: /origin set <player> <origin>"));
                return true;
            }
            try {
                Origin o = Origin.valueOf(args[2].toUpperCase());
                store.setOrigin(target.getUniqueId(), o);
                menus.giveWand(target);
                sender.sendMessage(mm("<green>" + target.getName() + " is now " + o.label() + "."));
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(mm("<red>Unknown origin."));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : List.of("wand", "use", "reset", "set")) if (s.startsWith(args[0].toLowerCase())) out.add(s);
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("set"))) {
            for (Player p : Bukkit.getOnlinePlayers()) out.add(p.getName());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            for (Origin o : Origin.values()) if (o.name().toLowerCase().startsWith(args[2].toLowerCase())) out.add(o.name().toLowerCase());
        }
        return out;
    }
}
