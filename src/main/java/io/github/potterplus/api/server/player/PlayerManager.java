package io.github.potterplus.api.server.player;

import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayerManager {

    @NonNull
    private final PotterPlusAPI api;

    public String getUsername(UUID uuid) {
        try {
            PreparedStatement ps = api.getDatabase().getConnection()
                    .prepareStatement("SELECT username FROM pp_users WHERE uuid=?");
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("username");
            } else {
                PluginLogger.atWarn("Could not find a username for uuid '" + uuid.toString() + "'");
            }
        } catch (SQLException e) {
            PluginLogger.atWarn("Failed to retrieve username for uuid '" + uuid.toString() + "'");
            e.printStackTrace();
        }

        return null;
    }

    public PotterPlayer getPlayer(UUID uuid) {
        return new PotterPlayer(uuid, api);
    }
}
