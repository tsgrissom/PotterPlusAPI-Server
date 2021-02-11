package io.github.potterplus.api.server.command.essential;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.color;

public class FeedCommand extends CommandBase<PotterPlusAPI> {

    public FeedCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "feed";
    }

    public void sendUsage(CommandContext context) {
        context.sendMessage(" &4&lX &cUsage&8: &c/" + context.getLabel() + " <player>");
    }

    public void feed(Player p) {
        p.setFoodLevel(20);
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.feed")) {
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

                feed(p);
                p.sendMessage(color("&dServer&8> &7Your hunger was sated"));
            }
        } else {
            context.sendMessage(color("&dServer&8> &7Feeding &e" + args.length + " &7people&8...."));

            for (String arg : args) {
                Optional<Player> target = context.resolveTarget();

                if (target.isPresent()) {
                    Player t = target.get();

                    feed(t);
                    t.sendMessage(color("&dServer&8> &7Your hunger was sated by &r" + cs.getName()));

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