package io.github.potterplus.api.server.command;

import com.google.common.collect.ImmutableMap;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;

import java.util.List;

public class MeCommand extends CommandBase<PotterPlusAPI> {

    public MeCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "me";
    }

    @Override
    public void execute(CommandContext context) {
        if (context.isPlayer()) {
            context.dispatchCommand("castp %pn% menu_character", ImmutableMap.of("%pn%", context.getPlayer().getName()));
        } else if (context.isConsole()) {
            context.sendMessage("Console does not have a character! Beep boop!");
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        return null;
    }
}
