package io.github.potterplus.api.server.gui;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.PaginatedUserInterface;
import io.github.potterplus.api.ui.button.UIButton;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KillListUI extends PaginatedUserInterface {

    @NonNull
    private final Player dispatcher;

    public KillListUI(Player dispatcher) {
        super("&8Who to kill?");

        this.dispatcher = dispatcher;

        refresh();
    }

    public void refresh() {
        refreshButtons();
        dispatcher.openInventory(getInventory());
    }

    public void refreshButtons() {
        this.clearButtons();

        for (Player p : Bukkit.getOnlinePlayers()) {
            addButton(createButton(p));
        }
    }

    public UIButton createButton(Player p) {
        Icon icon = Icon.skull(p)
                .name("&cKill " + p.getName())
                .lore(" &8> &6Click &7to kill");
        UIButton btn = new UIButton(icon);

        btn.setListener((e) -> {
            e.setCancelled(true);

            e.getWhoClicked().closeInventory();

            if (p.isOnline()) {
                ((Player) e.getWhoClicked()).performCommand("kill " + p.getName());
            } else {
                this.refresh();
            }
        });

        return btn;
    }
}
