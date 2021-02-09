package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RequiredArgsConstructor
public class LoginListener implements Listener {

    @NonNull
    private final PotterPlusAPI api;

    public Connection getConnection() {
        return api.getDatabase().getConnection();
    }

    private void recordLogin(PlayerLoginEvent event) throws SQLException {
        Player p = event.getPlayer();
        PotterPlayer pp = new PotterPlayer(p, api);
        String uuid = pp.getUniqueStr();
        String name = pp.getName();
        String fromIp = event.getAddress().getHostAddress();
        String toIp = api.getServer().getIp();
        long now = System.currentTimeMillis();

        if (!pp.hasRecordedLoginForAddress(fromIp)) {
            this.api.debug("Recording new login for player '" + name + "' at address " + fromIp);
        }

        PreparedStatement stmt = getConnection()
                .prepareStatement("INSERT INTO pp_logins " +
                "(to_ip, from_ip, uuid, join_time, result) " +
                "VALUES (?, ?, ?, ?, ?);");

        stmt.setString(1, toIp);
        stmt.setString(2, fromIp);
        stmt.setString(3, uuid);
        stmt.setLong(4, now);
        stmt.setString(5, event.getResult().name());

        stmt.executeUpdate();
    }

    @EventHandler
    public void onLogin(final PlayerLoginEvent event) {
        String name = event.getPlayer().getName();

        try {
            recordLogin(event);
        } catch (Throwable e) {
            PluginLogger.atWarn("Failed to update logins for PotterPlayer '" + name + "'");
            e.printStackTrace();
        }
    }
}
