package io.github.potterplus.api.server;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import io.github.potterplus.api.misc.PluginLogger;
import io.github.potterplus.api.server.command.LoginHistoryCommand;
import io.github.potterplus.api.server.command.PotterPlusAPICommand;
import io.github.potterplus.api.server.command.SetServerListCommand;
import io.github.potterplus.api.server.command.WhoIsCommand;
import io.github.potterplus.api.server.command.core.FeedCommand;
import io.github.potterplus.api.server.command.core.HealCommand;
import io.github.potterplus.api.server.listener.JoinListener;
import io.github.potterplus.api.server.listener.LoginListener;
import io.github.potterplus.api.server.listener.QuitListener;
import io.github.potterplus.api.server.listener.ServerListPingListener;
import io.github.potterplus.api.server.player.PlayerManager;
import io.github.potterplus.api.server.player.PotterPlayer;
import io.github.potterplus.api.server.storage.PotterPlusDBController;
import io.github.potterplus.api.string.StringUtilities;
import io.github.potterplus.api.ui.UserInterface;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.permission.Permissions;
import org.bukkit.plugin.java.annotation.plugin.*;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(
        name = "PotterPlusAPI",
        version = "1.0.6"
)
@ApiVersion(ApiVersion.Target.v1_13)
@Description("API used by PotterPlus to power its many plugins.")
@Author("T0xicTyler")
@Website("https://github.com/PotterPlus/PotterPlusAPI")
@DependsOn({@Dependency("Magic"), @Dependency("ProtocolLib"), @Dependency("LuckPerms")})
@LogPrefix("PPAPI")
@Commands({
        @Command(name = "loginhistory"),
        @Command(
                name = "potterplusapi",
                aliases = {"ppapi"}),
        @Command(name = "setserverlist",
                aliases = {"motd", "setmotd"}),
        @Command(name = "whois"),

        @Command(name = "heal"),
        @Command(name = "feed")
})
@Permissions({
        @Permission(name = "potterplus.admin"),
        @Permission(name = "potterplus.command.loginhistory"),
        @Permission(name = "potterplus.command.setserverlist"),
        @Permission(name = "potterplus.command.heal"),
        @Permission(name = "potterplus.command.feed")
})
public class PotterPlusAPI extends JavaPlugin {

    @Getter
    private PotterPlusAPI plugin;

    @Getter
    private PotterPlusDBController database;

    @Getter
    private PlayerManager playerManager;

    @Getter
    private MagicAPI magicAPI;

    @Getter
    private LuckPerms luckPerms;

    public static String getPrefix() {
        return "[PPAPI]";
    }

    public static String getColoredPrefix() {
        return StringUtilities.color(getPrefix());
    }

    public void debug(String str) {
        if (getConfig().getBoolean("debug")) {
            PluginLogger.atInfo(getPrefix() + " DEBUG - " + str);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("potterplus.admin")) {
                p.sendMessage(StringUtilities.color("&8| &dYou are receiving this notification from &3potterplus.admin"));
                p.sendMessage(StringUtilities.color("&8| " + str));
            }
        }
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

        pm.registerEvents(new LoginListener(this), this);
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new QuitListener(this), this);
        pm.registerEvents(new ServerListPingListener(this), this);
    }

    private void registerCommands() {
        new PotterPlusAPICommand(this);
        new WhoIsCommand(this);
        new LoginHistoryCommand(this);
        new SetServerListCommand(this);

        new HealCommand(this);
        new FeedCommand(this);
    }

    public PotterPlayer getPotterPlayer(OfflinePlayer player) {
        return new PotterPlayer(player, this);
    }

    @Override
    public void onEnable() {
        this.plugin = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

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
        database.disconnect();

        HandlerList.unregisterAll(this);
    }
}
