package io.github.potterplus.api.server.listener;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.wand.Wand;
import io.github.potterplus.api.server.PotterPlusAPI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class InteractListener implements Listener {

    @NonNull private final PotterPlusAPI api;
    @NonNull private final String shiftRightClickSpellKey, defaultWand;

    public String getCastCommand(Player player) {
        return "castp " + player.getName() + " " + shiftRightClickSpellKey;
    }

    @EventHandler
    public void onInteractWithWand(final PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Action a = event.getAction();
        ItemStack i = event.getItem();
        MagicAPI mapi = api.getMagicAPI();

        if (i == null) return;
        if (!api.getMagicAPI().isWand(i)) return;

        Wand w = mapi.getWand(i);
        String key  = w.getTemplateKey();

        if (key == null) return;
        if (!key.equalsIgnoreCase(defaultWand)) return;

        if (shiftRightClickSpellKey == null || shiftRightClickSpellKey.isEmpty()) return;
        if (!a.equals(Action.RIGHT_CLICK_AIR) && !a.equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!p.isSneaking()) return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getCastCommand(p));
    }
}
