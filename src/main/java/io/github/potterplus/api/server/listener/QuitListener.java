package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class QuitListener implements Listener {

    @NonNull private final PotterPlusAPI api;

    private Connection getConnection() {
        return api.getDatabase().getConnection();
    }

    private void updateQuitTime(PotterPlayer pp) throws SQLException {
        PreparedStatement stmt = getConnection()
                .prepareStatement("UPDATE pp_logins SET quit_time=? WHERE uuid=? AND locked=false;");
        stmt.setLong(1, System.currentTimeMillis());
        stmt.setString(2, pp.getUniqueStr());
        stmt.executeUpdate();
        api.debug("Updated quit time for session of '" + pp.getName() + "'");
    }

    private void lockSession(PotterPlayer pp) throws SQLException {
        PreparedStatement stmt = getConnection()
                .prepareStatement("UPDATE pp_logins SET locked=true WHERE uuid=? AND locked=false;");
        stmt.setString(1, pp.getUniqueStr());
        stmt.executeUpdate();
        api.debug("Locked session for user '" + pp.getName() + "'");
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player p = event.getPlayer();
        PotterPlayer pp = api.getPotterPlayer(p);

        try {
            updateQuitTime(pp);
            lockSession(pp);
        } catch (SQLException e) {
            PluginLogger.atSevere("Failed to close session for user '" + p.getName() + "'");
            e.printStackTrace();
        }
    }
}
