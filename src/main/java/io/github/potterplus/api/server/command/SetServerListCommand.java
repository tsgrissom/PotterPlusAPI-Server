package io.github.potterplus.api.server.command;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

import static io.github.potterplus.api.string.StringUtilities.equalsAny;

public class SetServerListCommand extends CommandBase<PotterPlusAPI> {

    public SetServerListCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    public String getLabel() {
        return "setserverlist";
    }

    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.setserverlist")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that.");
            return;
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            sendCurrent(context);
            context.sendMessage(" &4&lX &cUsage&8: &c/" + context.getLabel() + " <1,2> <text>");
            return;
        }

        String sub = context.getSub();
        int index;

        if (equalsAny(sub, "1", "2")) {
            index = Integer.parseInt(sub);

            if (args.length == 1) {
                context.sendMessage(" &4&lX &cEnter some text to set the server list MOTD to.");
            } else {
                String s = context.getMessage(1);

                setServerListLine(index, s);
                context.sendMessage("&dPPAPI&8> &7Set server list MOTD line &e#" + index);
            }
        } else {
            context.sendMessage(" &4&lX &cEnter a line number (1 or 2)");
        }

        sendCurrent(context);
    }

    public void sendCurrent(CommandContext context) {
        context.sendMessage("");
        context.sendMessage("&7Current MOTD&8:");
        context.sendMessage("");

        List<String> motd = getPlugin().getConfig().getStringList("motd");

        context.sendMessage(motd.get(0));
        context.sendMessage(motd.get(1));
    }

    public void setServerListLine(int index, String s) {
        index--;

        FileConfiguration config = getPlugin().getConfig();
        List<String> motd = config.isSet("motd") ? config.getStringList("motd") : new ArrayList<>();

        motd.set(index, s);

        config.set("motd", motd);

        getPlugin().saveConfig();
        getPlugin().debug("&7Set server list MOTD line " + (index + 1) + " to&8: &r" + StringUtilities.color(s));
    }

    public List<String> tab(CommandContext context) {
        return null;
    }
}
