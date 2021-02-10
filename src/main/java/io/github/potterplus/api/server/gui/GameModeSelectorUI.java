package io.github.potterplus.api.server.gui;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.UIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GameModeSelectorUI extends UserInterface {

    public GameModeSelectorUI() {
        super("&8Select your gamemode", 9);

        UIButton sel0 = new UIButton(Icon.start(Material.CAMPFIRE).name("&7Select&8: &bSurvival"));
        UIButton sel1 = new UIButton(Icon.start(Material.PURPLE_WOOL).name("&7Select&8: &bSurvival"));
        UIButton sel2 = new UIButton(Icon.start(Material.DIAMOND_SWORD).name("&7Select&8: &bAdventure"));
        UIButton sel3 = new UIButton(Icon.start(Material.GLASS).name("&7Select&8: &bSpectator"));

        sel0.setListener((e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).performCommand("gm0");
        }));

        sel1.setListener((e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).performCommand("gm1");
        }));

        sel2.setListener((e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).performCommand("gm2");
        }));

        sel3.setListener((e -> {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ((Player) e.getWhoClicked()).performCommand("gm3");
        }));

        setButton(1, sel0);
        setButton(3, sel1);
        setButton(6, sel2);
        setButton(8, sel3);
    }
}
