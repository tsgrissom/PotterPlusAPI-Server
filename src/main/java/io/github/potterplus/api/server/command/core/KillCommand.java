package io.github.potterplus.api.server.command.core;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.player.PlayerUtils;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.gui.KillListUI;
import io.github.potterplus.api.string.StringUtilities;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class KillCommand extends CommandBase<PotterPlusAPI> {

    public KillCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    public String getUsage() {
        return StringUtilities.color(" &4&lX &cUsage&8: &c/kill <player>");
    }

    @Override
    public String getLabel() {
        return "kill";
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.kill")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that!");

            return;
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            if (context.isPlayer()) {
                new KillListUI(context.getPlayer());
            } else {
                context.sendMessage(getUsage());
            }

            return;
        }

        for (String arg : args) {
            Optional<Player> target = context.resolveTarget(arg);

            if (!target.isPresent()) {
                context.sendMessage(" &4&lX Player '" + arg + "' is offline");

                return;
            }

            Player t = target.get();
            PlayerUtils pUtils = new PlayerUtils(t);

            t.damage(pUtils.getMaxHealth());

            context.sendMessage("&dServer&8> &7You killed player &e" + t.getName());
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        return PotterPlusServer.getOnlinePlayerNames();
    }
}
