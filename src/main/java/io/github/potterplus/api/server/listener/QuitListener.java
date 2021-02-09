package io.github.potterplus.api.server.listener;

import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class QuitListener implements Listener {

    @NonNull private final PotterPlusAPI api;

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Player p = event.getPlayer();
        PotterPlayer pp = api.getPotterPlayer(p);

        pp.closeSession();
    }
}
