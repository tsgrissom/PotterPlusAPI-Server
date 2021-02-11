package io.github.potterplus.api.server.command.essential;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.color;
import static io.github.potterplus.api.string.StringUtilities.getPrettyEnumName;

public class ClearPotionEffectsCommand extends CommandBase<PotterPlusAPI> {

    public ClearPotionEffectsCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    public String getUsage() {
        return color(" &4&lX &cUsage&8: &c/clearpots [player]");
    }

    @Override
    public String getLabel() {
        return "clearpotioneffects";
    }

    private void clearPotionEffects(CommandContext context, Player p) {
        Collection<PotionEffect> pots = p.getActivePotionEffects();

        if (pots.isEmpty()) {
            String perspective;

            if (p.equals(context.getSender())) {
                perspective = "&eYou &7have";
            } else {
                perspective = "&7Player &e" + p.getName() + " &7has";
            }

            context.sendMessage("&dServer&8> " + perspective + " no active potion effects.");

            return;
        }

        p.getActivePotionEffects().forEach(potionEffect -> {
            PotionEffectType pt = potionEffect.getType();
            context.sendMessage("&dServer&8> &7Removing &b" + getPrettyEnumName(pt.getName()) + " &7from &e" + p.getName());
            p.removePotionEffect(pt);
        });
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.clearpotioneffects")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that!");

            return;
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            if (context.isPlayer()) {
                clearPotionEffects(context, context.getPlayer());
            } else if (context.isConsole()) {
                context.sendMessage(getUsage());
            }

            return;
        }

        for (String arg : args) {
            Optional<Player> target = context.resolveTarget(arg);

            if (target.isPresent()) {
                clearPotionEffects(context, target.get());
            } else {
                context.sendMessage();
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        return PotterPlusServer.getOnlinePlayerNames();
    }
}
