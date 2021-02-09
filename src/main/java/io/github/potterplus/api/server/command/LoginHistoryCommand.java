package io.github.potterplus.api.server.command;

import io.github.potterplus.api.command.CommandBase;
import io.github.potterplus.api.command.CommandContext;
import io.github.potterplus.api.misc.TimeUtilities;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static io.github.potterplus.api.string.StringUtilities.color;

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
            Optional<Player> player = context.resolveTarget();

            if (player.isPresent()) {
                Player p = player.get();
                Connection con = api.getDatabase().getConnection();

                try {
                    PreparedStatement stmt = con.prepareStatement("SELECT * FROM pp_logins WHERE uuid=?");

                    stmt.setString(1, p.getUniqueId().toString());

                    getPlugin().debug("Getting login history for player '" + context.getSenderName() + "'");

                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        String ip = rs.getString("ip");
                        String uuid = rs.getString("uuid");
                        long time = rs.getLong("time");
                        String result = rs.getString("result");

                        String ipColored = color("&7Address&8: &e" + ip + "\n");
                        String uuidColored = color("&7UUID&8: &e" + uuid + "\n");
                        String resultColored = color("&7Result&8: &e" + result);
                        String timeColored = color("&e" + TimeUtilities.prettyTime(time));

                        TextComponent msg = new TextComponent(color("&6> &7Login at &e" + timeColored));

                        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ipColored), new Text(uuidColored), new Text(resultColored)));

                        context.getPlayer().spigot().sendMessage(msg);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
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
