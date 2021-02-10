package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class JoinListener implements Listener {

    @NonNull private final PotterPlusAPI api;

    public Connection getConnection() {
        return api.getDatabase().getConnection();
    }

    private void updateDB(PlayerJoinEvent event) throws SQLException {
        Player p = event.getPlayer();
        PotterPlayer pp = new PotterPlayer(p, api);
        String uuid = pp.getUniqueStr();
        String name = pp.getName();
        String fromIp = pp.getIpAddress();

        PreparedStatement stmt = getConnection()
                .prepareStatement("SELECT id FROM pp_users WHERE uuid=?;");

        stmt.setString(1, uuid);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            this.api.debug("Found existing record of player '" + name + "'. Updating username.");

            stmt = getConnection().prepareStatement("UPDATE pp_users SET username=? WHERE uuid=?;");

            stmt.setString(1, name);
            stmt.setString(2, uuid);
            stmt.executeUpdate();

            this.api.debug("Updated player record for '" + name + "'");
        } else {
            this.api.debug("Found no record of player '" + name + "'. Creating database entry...");

            stmt = getConnection().prepareStatement("INSERT INTO pp_users (uuid, ip, username) VALUES (?, ?, ?);");

            stmt.setString(1, uuid);
            stmt.setString(2, fromIp);
            stmt.setString(3, name);

            stmt.executeUpdate();

            this.api.debug("Created player record for player '" + name + "'");
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (api.getConfig().getBoolean("features.auto_rp", true)) {
            p.performCommand("getrp");
        }

        String name = p.getName();

        try {
            updateDB(event);
        } catch (SQLException e) {
            PluginLogger.atWarn("Failed to update DB record for PotterPlayer '" + name + "'");
            e.printStackTrace();
        }
    }
}
