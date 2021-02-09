package io.github.potterplus.api.server.command;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.misc.BooleanFormatter;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.string.StringUtilities;
import lombok.NonNull;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.List;

public class PotterPlusAPICommand extends CommandBase<PotterPlusAPI> {

    public PotterPlusAPICommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "potterplusapi";
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.admin")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that!");
            return;
        }

        String[] args = context.getArgs();

        if (args.length == 0) {
            context.sendMessage("&4&lX &cUsage&8: &c/ppapi debug|reload|version");
        } else {
            String arg1 = args[0];

            if (StringUtilities.equalsAny(arg1, "reload", "load")) {
                try {
                    getPlugin().reloadConfig();

                    context.sendMessage("&dPotterPlusAPI&8> &aPlugin reloaded!");

                    if (context.getSender().hasPermission("potterplus.admin")) {
                        boolean debug = getPlugin().getConfig().getBoolean("debug");
                        context.sendMessage("&dPPAPI&8> &7Debugging is " + BooleanFormatter.ENABLED_DISABLED.format(debug));
                    }
                } catch (Exception e) {
                    context.sendMessage("&dPotterPlusAPI&8> &cFailed to reload! Check console for information.");
                    e.printStackTrace();
                }
            } else if (StringUtilities.equalsAny(arg1, "debug")) {
                boolean debug = !getPlugin().getConfig().getBoolean("debug");

                getPlugin().getConfig().set("debug", debug);
                getPlugin().saveConfig();

                context.sendMessage("&dPPAPI&8> &7Debugging " + BooleanFormatter.ENABLED_DISABLED.format(debug));
            } else if (StringUtilities.equalsAny(arg1, "version", "v")) {
                PluginDescriptionFile pdFile = getPlugin().getDescription();
                String version = pdFile.getVersion();

                context.sendMessage("&dPPAPI&8> &7Running version &e" + version);
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        // TODO Tab completion
        return null;
    }
}
