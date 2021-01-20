package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.misc.StringUtilities;
import java.util.Collections;
import java.util.List;

import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

@RequiredArgsConstructor
public class ServerListPingListener implements Listener {

    @NonNull
    private final PotterPlusAPI api;

    @EventHandler
    public void onPing(ServerListPingEvent event) {
        FileConfiguration config = api.getConfig();
        List<String> motd = null;

        if (!config.isSet("motd") || (config.isSet("motd") && config.getStringList("motd").get(0) == null)) {
            motd = Collections.singletonList(StringUtilities.color("&cMOTD not set"));
        } else {
            motd = config.getStringList("motd");
        }

        if (motd.size() > 1) {
            event.setMotd(StringUtilities.color(motd.get(0) + "\n" + (String)motd.get(1)));
        } else {
            event.setMotd(StringUtilities.color(motd.get(0)));
        }
    }
}
