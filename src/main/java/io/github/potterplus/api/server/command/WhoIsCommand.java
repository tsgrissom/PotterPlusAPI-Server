package io.github.potterplus.api.server.command;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.gui.WhoIsGUI;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.List;
import java.util.Optional;

public class WhoIsCommand extends CommandBase<PotterPlusAPI> {

    public static final Permission PERMISSION_WHOIS = new Permission("potterplus.command.whois");

    public WhoIsCommand(@NonNull PotterPlusAPI api) {
        super(api);
    }

    @Override
    public String getLabel() {
        return "whois";
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission(PERMISSION_WHOIS)) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that.");
            return;
        }

        if (!(context.getSender() instanceof Player)) {
            // TODO Whois for console

            return;
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            context.sendMessage(" &4&lX &cUsage&8: &c/" + context.getLabel() + " <player>");
        } else {
            String sub = context.getSub();
            Optional<Player> player = context.resolveTarget();

            if (player.isPresent()) {
                new WhoIsGUI(getPlugin(), player.get()).activate(context.getPlayer());
            } else {
                context.sendMessage(" &4&lX &cUnknown player '" + sub + "'");
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        return null;
    }
}
