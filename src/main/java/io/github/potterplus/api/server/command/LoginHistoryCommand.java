package io.github.potterplus.api.server.command;

import io.github.potterplus.api.PotterPlusServer;
import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.misc.TimeUtilities;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import io.github.potterplus.api.string.HoverMessage;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LoginHistoryCommand extends CommandBase<PotterPlusAPI> {

    public LoginHistoryCommand(@NonNull PotterPlusAPI plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "loginhistory";
    }

    @Override
    public void execute(CommandContext context) {
        if (!context.hasPermission("potterplus.command.loginhistory")) {
            context.sendMessage(" &4&lX &cYou are not allowed to do that.");
            return;
        }

        PotterPlusAPI api = getPlugin();
        String[] args = context.getArgs();

        if (args.length == 0) {
            context.sendMessage(" &4&lX &cUsage&8: &c/" + context.getLabel() + " <player>");
        } else {
            String arg1 = args[0];
            PotterPlayer pp = PotterPlayer.of(arg1, getPlugin());

            if (pp != null) {
                Connection con = api.getDatabase().getConnection();

                try {
                    PreparedStatement stmt = con.prepareStatement("SELECT * FROM pp_logins WHERE uuid=?");

                    stmt.setString(1, pp.getUniqueStr());

                    getPlugin().debug("Getting login history for player '" + context.getSenderName() + "'");

                    ResultSet rs = stmt.executeQuery();

                    context.sendMessage(" &6&lOLDEST");

                    while (rs.next()) {
                        String fromIp = rs.getString("from_ip");
                        String uuid = rs.getString("uuid");
                        String result = rs.getString("result");
                        long joinTime = rs.getLong("join_time");
                        long quitTime = rs.getLong("quit_time");
                        boolean locked = rs.getBoolean("locked");
                        Date joinDate = new Date(joinTime);
                        Date quitDate = new Date(quitTime);

                        HoverMessage hm = HoverMessage
                                .compose("&7> &7Login &e" + TimeUtilities.prettyTime(joinTime) + " &7(&3" + TimeUtilities.format(joinTime) + "&7)");

                        hm.withHover("&7From IP&8: &e" + fromIp);
                        hm.withHover("&7UUID&8: &e" + uuid);
                        hm.withHover("&7Result&8: &e" + result);
                        hm.withHover("&8&m----------------------------------------");

                        if (!locked && pp.isOnline()) {
                            hm.withHover("&a&lActive session");
                        } else {
                            hm.withHover("&3Timings");
                            hm.withHover("&7Join time&8:");
                            hm.withHover(" &e" + TimeUtilities.prettyTime(joinTime) + " &7(&3" + TimeUtilities.format(joinTime) + "&7)");
                            hm.withHover("&7Quit time&8:");

                            String quitTimeStr = quitTime == 0 ? " &cUndefined" : " &e" + TimeUtilities.prettyTime(quitTime) + " &7(&3" + TimeUtilities.format(quitTime) + "&7)";

                            hm.withHover(quitTimeStr);
                            hm.withHover("&7Session length&8:");
                            hm.withHover(" &e" + TimeUtilities.prettyDuration(joinDate, quitDate));
                        }

                        hm.send(pp.getPlayer());
                    }

                    context.sendMessage(" &6&lNEWEST");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> tab(CommandContext context) {
        Set<String> set = new HashSet<>();

        if (context.hasPermission("potterplus.admin")) {
            try {
                set.addAll(getPlugin().getPlayerManager().getAllUsernames());
            } catch (SQLException e) {
                getPlugin().debug("Failed to complete tab for command /loginhistory");
                e.printStackTrace();
            }
        }

        set.addAll(PotterPlusServer.getOnlinePlayerNames());

        return new ArrayList<>(set);
    }
}
