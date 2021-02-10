package io.github.potterplus.api.server.command;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class WhereCommand extends CommandBase<PotterPlusAPI> {

    public WhereCommand(@NonNull PotterPlusAPI api) {
        super(api);
    }

    @Override
    public String getLabel() {
        return "where";
    }

    @Override
    public void execute(CommandContext context) {
        FileConfiguration c = getPlugin().getConfig();
        String serverName = c.getString("server_name");
        context.sendMessage("&dServer&8> &e" + context.getSenderName() + "&8, &7you are on the &r" + serverName + " &7server&8.");
    }

    @Override
    public List<String> tab(CommandContext context) {
        return null;
    }
}
