package io.github.potterplus.api.server.command;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.gui.WhoIsGUI;
import io.github.potterplus.api.server.player.PotterPlayer;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.annotation.command.Command;

import java.sql.SQLException;
import java.util.*;

public class WhoIsCommand extends CommandBase<PotterPlusAPI> {

    public static final Permission PERMISSION_WHOIS = new Permission("potterplus.command.whois");

    public WhoIsCommand(@NonNull PotterPlusAPI api) {
        super(api);
    }

    @Override
    public String getLabel() {
        return "whois";
    }

    private void executePlayerCommand(CommandContext context) {
        Player p = context.getPlayer();
        String label = context.getLabel();
        String[] args = context.getArgs();

        if (args.length == 0) {
            context.sendMessage(" &4&lX &cUsage&8: &c/" + label + " <player>");
        } else {
            String arg1 = context.getSub();
            PotterPlayer player = PotterPlayer.of(arg1, getPlugin());

            if (player != null) {
                new WhoIsGUI(getPlugin(), player).activate(p);
            } else {
                context.sendMessage(" &4&lX &cUnknown player '" + arg1 + "'");
            }
        }
    }

    private void executeConsoleCommand(CommandContext context) {
        context.sendMessage("&cNo console command yet");
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission(PERMISSION_WHOIS)) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that.");
            return;
        }

        CommandSender sender = context.getSender();

        if (sender instanceof Player) {
            executePlayerCommand(context);
        } else if (sender instanceof ConsoleCommandSender) {
            executeConsoleCommand(context);
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        Set<String> set = new HashSet<>();

        if (context.hasPermission("potterplus.admin")) {
            try {
                set.addAll(getPlugin().getPlayerManager().getAllUsernames());
            } catch (SQLException e) {
                context.sendMessage("&cSorry, you had &4potterplus.admin &cbut we couldn't retrieve the list of players!");
                e.printStackTrace();
            }
        }

        set.addAll(PotterPlusServer.getOnlinePlayerNames());

        return new ArrayList<>(set);
    }
}
