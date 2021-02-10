package io.github.potterplus.api.server.command.core;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.gui.GameModeSelectorUI;
import io.github.potterplus.api.string.StringUtilities;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static io.github.potterplus.api.string.StringUtilities.*;

public class GameModeCommand extends CommandBase<PotterPlusAPI> {

    public GameModeCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "gamemode";
    }

    public String getUsage() {
        return " &4&lX &cUsage&8: &c/gm <0|1|2|3> [player]";
    }

    private Optional<GameMode> parseGameMode(String s) {
        s = s.toLowerCase();
        String[] survival = new String[]{"survival", "0", "s"};
        String[] creative = new String[]{"creative", "1", "c"};
        String[] adventure = new String[]{"adventure", "2", "a"};
        String[] spectator = new String[]{"spectator", "3", "sp"};

        if (Arrays.asList(survival).contains(s)) {
            return Optional.of(GameMode.SURVIVAL);
        } else if (Arrays.asList(creative).contains(s)) {
            return Optional.of(GameMode.CREATIVE);
        } else if (Arrays.asList(adventure).contains(s)) {
            return Optional.of(GameMode.ADVENTURE);
        } else if (Arrays.asList(spectator).contains(s)) {
            return Optional.of(GameMode.SPECTATOR);
        }

        return Optional.empty();
    }

    private String prettyName(GameMode gm) {
        return StringUtilities.color("&b" + getPrettyEnumName(gm.name()));
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.gamemode")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that!");

            return;
        }

        CommandSender s = context.getSender();
        String label = context.getLabel();
        String[] args = context.getArgs();

        if (equalsAny(label, "gamemode", "gm")) {
            if (args.length == 0) {
                if (context.isPlayer()) {
                    new GameModeSelectorUI().activate(context.getPlayer());
                } else if (context.isConsole()) {
                    context.sendMessage(getUsage());
                }
            } else {
                if (context.isConsole()) {
                    context.sendMessage(getUsage());

                    return;
                }

                String arg1 = args[0];
                Optional<GameMode> gameMode = parseGameMode(arg1);

                if (!gameMode.isPresent()) {
                    context.sendMessage(" &4&lX &cUnknown gamemode '" + arg1 + "'");

                    return;
                }

                GameMode gm = gameMode.get();
                Map<String, String> replace = new HashMap<>();

                replace.put("%gm%", prettyName(gm));
                replace.put("%pn%", context.getSenderName());

                if (args.length == 1) {
                    if (context.isPlayer()) {
                        Player p = context.getPlayer();

                        p.setGameMode(gm);

                        context.sendMessage(replace("&dServer&8> &7You set your gamemode to &b%gm%", replace));
                    }
                } else {
                    for (int i = 1; i < args.length; i++) {
                        Optional<Player> target = context.resolveTarget(args[i]);

                        if (target.isPresent()) {
                            Player t = target.get();

                            replace.put("%tn%", t.getName());

                            t.setGameMode(gm);
                            t.sendMessage(color(replace("&dServer&8> &7Your gamemode was set to &b%gm% &7by &e%pn%", replace)));

                            context.sendMessage(replace("&dServer&8> &7You set &e%tn%'s &7gamemode to &b%gm%", replace));
                        } else {
                            context.sendMessage(" &4&lX &cPlayer '" + args[i] + "' is offline");
                        }
                    }
                }
            }
        } else if (equalsAny(label, "gm0", "gm1", "gm2", "gm3", "gms", "gmc", "gma", "gmsp", "gmsurvival", "gmcreative", "gmadventure", "gmspectator")) {
            String gmString = label.substring(2);

            if (args.length == 0) {
                if (context.isPlayer()) {
                    context.performCommand(String.format("gm %s", gmString));
                } else if (context.isConsole()) {
                    context.sendMessage(getUsage());
                }
            } else {
                for (String arg : args) {
                    context.performCommand(String.format("gm %s %s", gmString, arg));
                }
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        // TODO Tab completion
        return null;
    }
}
