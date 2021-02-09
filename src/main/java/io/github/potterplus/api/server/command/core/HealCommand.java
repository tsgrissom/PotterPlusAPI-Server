package io.github.potterplus.api.server.command.core;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.wrapper.PPCommandSender;
import lombok.NonNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.color;

public class HealCommand extends CommandBase<PotterPlusAPI> {

    public HealCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "heal";
    }

    public void sendUsage(CommandContext context) {
        context.sendMessage(" &4&lX &cUsage&8: &c/" + context.getLabel() + " <player>");
    }

    public void heal(Player p) {
        AttributeInstance maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double d;

        if (maxHealth == null) {
            d = 20;
        } else {
            d = maxHealth.getValue();
        }

        p.setHealth(d);
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.heal")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that.");

            return;
        }

        CommandSender cs = context.getSender();
        String[] args = context.getArgs();

        if (args.length == 0) {
            if (context.isConsole()) {
                sendUsage(context);

                return;
            }

            if (context.isPlayer()) {
                Player p = (Player) cs;

                heal(p);
                p.sendMessage(color("&dServer&8> &7You healed yourself" ));
            }
        } else {
            context.sendMessage("&dServer&8> &7Healing &e" + args.length + " &7people&8....");

            for (String arg : args) {
                Optional<Player> target = context.resolveTarget();

                if (target.isPresent()) {
                    Player t = target.get();

                    heal(t);
                    t.sendMessage(color("&dServer&8> &7You were healed by &r" + cs.getName()));

                    context.sendMessage(" &7- " + t.getDisplayName());
                } else {
                    context.sendMessage(" &4&lX &cPlayer '" + arg + "' is offline");
                }
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        return PotterPlusServer.getOnlinePlayerNames();
    }
}