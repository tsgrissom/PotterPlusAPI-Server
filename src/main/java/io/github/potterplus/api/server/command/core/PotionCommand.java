package io.github.potterplus.api.server.command.core;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.string.StringUtilities;
import lombok.NonNull;

import java.util.List;

public class PotionCommand extends CommandBase<PotterPlusAPI> {

    public PotionCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "potion";
    }

    public String getUsage() {
        return StringUtilities.color(" &4&lX &cUsage&8: &c/pot <type> [amplifier] [duration]");
    }

    @Override
    public void execute(CommandContext context) {
        context.sendMessage(getUsage());

        // TODO Finish this command
    }

    @Override
    public List<String> tab(CommandContext context) {
        return null;
    }
}
