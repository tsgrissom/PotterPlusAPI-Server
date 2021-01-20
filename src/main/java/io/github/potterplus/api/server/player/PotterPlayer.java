package io.github.potterplus.api.server.player;

import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.storage.PotterPlusDBController;
import lombok.NonNull;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("SqlResolve")
public class PotterPlayer {

    @NonNull
    private final OfflinePlayer player;

    @NonNull
    private final PotterPlusAPI api;

    public PotterPlayer(OfflinePlayer offlinePlayer, PotterPlusAPI api) {
        this.player = offlinePlayer;
        this.api = api;
    }

    public PotterPlayer(UUID uuid, PotterPlusAPI api) {
        this(Bukkit.getOfflinePlayer(uuid), api);
    }

    public PotterPlayer(String uuid, PotterPlusAPI api) {
        this(UUID.fromString(uuid), api);
    }

    public PotterPlusDBController getDatabase() {
        return api.getDatabase();
    }

    private Connection getConnection() {
        return getDatabase().getConnection();
    }

    public ResultSet getDBUser() {
        ResultSet rs = null;

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_users WHERE uuid=?");

            stmt.setString(1, getUniqueId().toString());

            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public ResultSet getLoginHistory() {
        ResultSet rs = null;

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_logins WHERE uuid=?");

            stmt.setString(1, getUniqueId().toString());

            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public ResultSet getFirstLogin() {
        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_logins WHERE uuid=? ORDER BY id LIMIT 1;");

            stmt.setString(1, getUniqueId().toString());

            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getPlayingSince() {
        long unix = 0;
        ResultSet rs = getFirstLogin();

        try {
            if (rs.next()) {
                unix = rs.getLong("time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unix;
    }

    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public House getHouse() {
        if (!player.isOnline()) {
            return House.UNSORTED;
        }

        Player p = (Player) player;

        for (House house : House.values()) {
            if (house.equals(House.UNSORTED) || house.getPermissionNode().equals("group.default")) {
                continue;
            }

            Permission perm = house.getPermission();

            if (p.hasPermission(perm)) {
                return house;
            }
        }

        return House.UNSORTED;
    }

    public Role getRole() {
        if (!player.isOnline()) {
            return Role.STUDENT;
        }

        Player p = (Player) player;

        if (p.hasPermission("group.admin")) {
            return Role.HEAD;
        } else if (p.hasPermission("group.mod")){
            return Role.PREFECT;
        } else {
            return Role.STUDENT;
        }
    }

    public User getLPUser() {
        return api.getLuckPerms().getUserManager().getUser(player.getUniqueId());
    }
}
