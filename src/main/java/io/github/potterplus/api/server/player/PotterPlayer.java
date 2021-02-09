package io.github.potterplus.api.server.player;

import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.storage.PotterPlusDBController;
import lombok.Getter;
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

import static io.github.potterplus.api.misc.PluginLogger.atWarn;

@SuppressWarnings("SqlResolve")
public class PotterPlayer {

    public static PotterPlayer of(String name, PotterPlusAPI api) {
        api.debug("Attempting to fetch PotterPlayer by username");

        try {
            PreparedStatement stmt = api.getDatabase().getConnection().prepareStatement("SELECT * FROM pp_users WHERE username=?;");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                api.debug("Record for username '" + name + "' found");

                UUID uuid = UUID.fromString(rs.getString("uuid"));

                return new PotterPlayer(Bukkit.getOfflinePlayer(uuid), api);
            } else {
                api.debug("No record found for username '" + name + "'");
            }
        } catch (SQLException e) {
            atWarn("Failed to look up PotterPlayer '" + name + "' via username");
            e.printStackTrace();
        }

        return null;
    }

    @NonNull @Getter
    private final OfflinePlayer offlinePlayer;

    @NonNull
    private final PotterPlusAPI api;

    public PotterPlayer(OfflinePlayer offlinePlayer, PotterPlusAPI api) {
        this.offlinePlayer = offlinePlayer;
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

    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    public Player getPlayer() {
        Player p = (Player) getOfflinePlayer();

        if (!p.isOnline()) {
            throw new NullPointerException("Cannot get Player from offline PotterPlayer!");
        }

        return p;
    }

    public ResultSet getDBUser() {
        ResultSet rs = null;

        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_users WHERE uuid=?");

            stmt.setString(1, getUniqueStr());

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

            stmt.setString(1, getUniqueStr());

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

            stmt.setString(1, getUniqueStr());

            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean hasRecordedLoginForAddress(String address) {
        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_logins WHERE uuid=? AND from_ip=?");
            stmt.setString(1, getUniqueStr());
            stmt.setString(2, address);

            if (stmt.executeQuery().next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public long getPlayingSince() {
        long unix = 0;
        ResultSet rs = getFirstLogin();

        try {
            if (rs.next()) {
                unix = rs.getLong("join_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unix;
    }

    public ResultSet getUser() {
        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT * FROM pp_users WHERE uuid=?;");
            stmt.setString(1, getUniqueStr());
            return stmt.executeQuery();
        } catch (SQLException e) {
            atWarn("Failed to retrieve PotterPlayer DB record for '" + getName() + "'");
            e.printStackTrace();
        }

        return null;
    }

    public UUID getUniqueId() {
        return getOfflinePlayer().getUniqueId();
    }

    public String getUniqueStr() {
        return getUniqueId().toString();
    }

    public String getName() {
        return getOfflinePlayer().getName();
    }

    public String getIpAddress() {
        try {
            PreparedStatement stmt = getConnection()
                    .prepareStatement("SELECT ip FROM pp_users WHERE uuid=?;");
            stmt.setString(1, getUniqueStr());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("ip");
            }
        } catch (SQLException e) {
            atWarn("Failed to get IP address from DB for PotterPlayer '" + getName() + "'");
            e.printStackTrace();
        }

        return null;
    }

    public House getHouse() {
        if (!getOfflinePlayer().isOnline()) {
            return House.UNSORTED;
        }

        Player p = (Player) getOfflinePlayer();

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
        if (!getOfflinePlayer().isOnline()) {
            return Role.STUDENT;
        }

        Player p = (Player) getOfflinePlayer();

        if (p.hasPermission("group.admin")) {
            return Role.HEAD;
        } else if (p.hasPermission("group.mod")){
            return Role.PREFECT;
        } else {
            return Role.STUDENT;
        }
    }

    public boolean isStudent() {
        return getRole().equals(Role.STUDENT);
    }

    public boolean isStaff() {
        return getRole().equals(Role.HEAD) || getRole().equals(Role.PREFECT);
    }

    public User getLPUser() {
        return api.getLuckPerms().getUserManager().getUser(getOfflinePlayer().getUniqueId());
    }
}
