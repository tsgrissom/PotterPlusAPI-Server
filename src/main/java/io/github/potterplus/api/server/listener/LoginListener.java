package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class LoginListener implements Listener {

    @NonNull
    private final PotterPlusAPI api;

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        Player p = event.getPlayer();
        Connection connection = api.getDatabase().getConnection();

        String uuid = p.getUniqueId().toString();
        String name = p.getName();
        String ip = event.getAddress().getHostAddress();

        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO pp_logins (ip, uuid, time, result) VALUES (?, ?, ?, ?);");

            stmt.setString(1, ip);
            stmt.setString(2, uuid);
            stmt.setLong(3, System.currentTimeMillis());
            stmt.setString(4, event.getResult().name());

            stmt.executeUpdate();

            this.api.debug("Recorded login for player '" + name + "' at address " + ip);
        } catch (Throwable e) {
            PluginLogger.atWarn("Failed to update logins for PotterPlayer '" + name + "'");
            e.printStackTrace();
        }
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM pp_users WHERE uuid=?;");

            stmt.setString(1, uuid);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                this.api.debug("Found existing record of player '" + name + "'. Updating username.");

                stmt = connection.prepareStatement("UPDATE pp_users SET username=? WHERE uuid=?;");

                stmt.setString(1, name);
                stmt.setString(2, uuid);
                stmt.executeUpdate();

                this.api.debug("Updated player record for '" + name + "'");
            } else {
                this.api.debug("Found no record of player '" + name + "'. Creating database entry...");

                stmt = connection.prepareStatement("INSERT INTO pp_users (uuid, ip, username) VALUES (?, ?, ?);");

                stmt.setString(1, uuid);
                stmt.setString(2, ip);
                stmt.setString(3, name);

                stmt.executeUpdate();

                this.api.debug("Created player record for player '" + name + "'");
            }
        } catch (SQLException e) {
            PluginLogger.atWarn("Failed to update DB record for PotterPlayer '" + name + "'");
            e.printStackTrace();
        }
    }
}
