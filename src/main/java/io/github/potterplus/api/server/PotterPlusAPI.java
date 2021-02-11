package io.github.potterplus.api.server;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import io.github.potterplus.api.PotterPlugin;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.command.*;
import io.github.potterplus.api.server.command.essential.*;
import io.github.potterplus.api.server.listener.*;
import io.github.potterplus.api.server.player.PlayerManager;
import io.github.potterplus.api.server.player.PotterPlayer;
import io.github.potterplus.api.server.storage.db.PotterPlusDBController;
import io.github.potterplus.api.server.storage.flatfile.DatabaseFile;
import io.github.potterplus.api.ui.UserInterface;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.github.potterplus.api.misc.PluginLogger.atInfo;
import static io.github.potterplus.api.misc.PluginLogger.atSevere;

@Plugin(
        name = "PotterPlusAPI",
        version = "1.0.7"
)
@ApiVersion(ApiVersion.Target.v1_13)
@Description("API used by PotterPlus to power its many plugins.")
@Author("T0xicTyler")
@Website("https://github.com/PotterPlus/PotterPlusAPI")
@DependsOn({@Dependency("Magic"), @Dependency("ProtocolLib"), @Dependency("LuckPerms")})
@LogPrefix("PPAPI")
@Commands({
        @Command(name = "clearpotioneffects",
                aliases = {"clearpots", "clearstatuses", "clearstatuseffects"}),
        @Command(name = "feed",
                aliases = {"sate"}),
        @Command(name = "gamemode",
                aliases = {"gm", "gm0", "gm1", "gm2", "gm3", "gms", "gmc", "gma", "gmsp", "gmsurvival", "gmcreative", "gmadventure", "gmspectator"}),
        @Command(name = "heal"),
        @Command(name = "kill"),
        @Command(name = "potion",
                aliases = {"pot", "applypot", "applypotion", "applypotioneffect", "applystatus", "applystatuseffect"}),
        @Command(name = "loginhistory",
                aliases = {"lh"}),
        @Command(name = "me",
                aliases = {"persona", "character", "whoami"}),
        @Command(name = "potterplusapi",
                aliases = {"ppapi"}),
        @Command(name = "setserverlist",
                aliases = {"motd", "setmotd"}),
        @Command(name = "where",
                aliases = {"whereami"}),
        @Command(name = "whois",
                aliases = {"who"})
})
@Permissions({
        @Permission(name = "potterplus.admin"),

        @Permission(name = "potterplus.command.clearpotioneffects"),
        @Permission(name = "potterplus.command.feed"),
        @Permission(name = "potterplus.command.gamemode"),
        @Permission(name = "potterplus.command.heal"),
        @Permission(name = "potterplus.command.kill"),
        @Permission(name = "potterplus.command.potion"),

        @Permission(name = "potterplus.command.loginhistory"),
        @Permission(name = "potterplus.command.setserverlist"),
})
public class PotterPlusAPI extends PotterPlugin {

    @Getter
    private PotterPlusAPI plugin;

    @Getter
    private PotterPlusDBController database;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private DatabaseFile databaseFile;

    @Getter
    private MagicAPI magicAPI;

    @Getter
    private LuckPerms luckPerms;

    public String getLogPrefix() {
        return "[PPAPI]";
    }

    public boolean isFeatureEnabled(String feature) {
        return getConfig().getBoolean("features." + feature, true);
    }

    private void setupDependencies() {
        PluginManager pm = Bukkit.getPluginManager();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            this.luckPerms = provider.getProvider();
        } else {
            PluginLogger.atWarn("Required dependency LuckPerms not present.");
            Bukkit.shutdown();
        }

        if (pm.isPluginEnabled("Magic")) {
            this.magicAPI = (MagicAPI)pm.getPlugin("Magic");

            PluginLogger.atInfo("Hooked into Magic.");
        } else {
            PluginLogger.atWarn("Required dependency Magic not present.");
            Bukkit.shutdown();
        }
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        FileConfiguration c = getConfig();

        if (isFeatureEnabled("motd")) {
            pm.registerEvents(new ServerListPingListener(this), this);
        }

        if (isFeatureEnabled("db_logins")) {
            pm.registerEvents(new LoginListener(this), this);
        }

        pm.registerEvents(new JoinListener(this), this);

        if (isFeatureEnabled("db_logins")) {
            pm.registerEvents(new QuitListener(this), this);
        }

        if (isFeatureEnabled("shift_right_click_menu")) {
            String shiftRightClickCast = c.getString("shift_right_click_cast", "menu_character");
            String defaultWand = c.getString("default_wand", "playerwand");

            pm.registerEvents(new InteractListener(this, shiftRightClickCast, defaultWand), this);
        }
    }

    private void registerCommands() {
        new ClearPotionEffectsCommand(this);
        new FeedCommand(this);
        new GameModeCommand(this);
        new HealCommand(this);
        new KillCommand(this);
        new PotionCommand(this);

        new LoginHistoryCommand(this);
        new MeCommand(this);
        new PotterPlusAPICommand(this);
        new SetServerListCommand(this);
        new WhereCommand(this);
        new WhoIsCommand(this);
    }

    public PotterPlayer getPotterPlayer(OfflinePlayer player) {
        return new PotterPlayer(player, this);
    }

    private void closeSessions() {
        Connection c = getDatabase().getConnection();

        try {
            debug("Checking for loose sessions.");
            PreparedStatement checkForSessions = c
                    .prepareStatement("SELECT id FROM pp_logins WHERE locked=false OR quit_time=0");

            if (checkForSessions.executeQuery().next()) {
                debug("Found remaining sessions. Updating and locking...");

                try {
                    PreparedStatement updateQuitTimes = getDatabase().getConnection()
                            .prepareStatement("UPDATE pp_logins SET quit_time=? WHERE locked=false;");
                    updateQuitTimes.setLong(1, System.currentTimeMillis());
                    PreparedStatement lock = getDatabase().getConnection()
                            .prepareStatement("UPDATE pp_logins SET locked=true WHERE locked=false;");
                    updateQuitTimes.executeUpdate();
                    lock.executeUpdate();
                    atInfo("Locked remaining PotterPlayer sessions.");
                } catch (SQLException e) {
                    atSevere("Failed to update and lock remaining sessions!");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            atSevere("Failed to check for loose sessions.");
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        this.databaseFile = new DatabaseFile(this);

        this.setupDependencies();

        UserInterface.prepare(this);

        this.registerEvents();
        this.registerCommands();

        this.database = new PotterPlusDBController(this);

        database.connect();

        this.playerManager = new PlayerManager(this);
    }

    @Override
    public void onDisable() {
        closeSessions();

        database.disconnect();

        HandlerList.unregisterAll(this);
    }
}
