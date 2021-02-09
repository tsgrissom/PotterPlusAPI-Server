package io.github.potterplus.api.server.gui;

import io.github.potterplus.api.gui.GUI;
import io.github.potterplus.api.gui.button.AutoGUIButton;
import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.TimeUtilities;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import io.github.potterplus.api.server.player.Role;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhoIsGUI extends GUI {

    public WhoIsGUI(PotterPlusAPI api, PotterPlayer pp) {
        super("&8Who is " + pp.getRole().getColorPrefix() + pp.getName() + "&8?", 18);

        List<String> lore = new ArrayList<>();

        lore.add("&7House&8: &r" + pp.getHouse().getColoredName());
        lore.add("&7Role&8: &r" + pp.getRole().getColoredName());

        if (pp.isOnline()) {
            Player p = pp.getPlayer();

            lore.add("");
            lore.add("&7Display name&8: &r" + p.getDisplayName());
        }

        lore.add("");
        lore.add("&7Playing since&8: &e" + new SimpleDateFormat(TimeUtilities.PATTERN)
                .format(new Date(pp.getPlayingSince())));

        Icon skull = Icon
                .skull(pp.getUniqueId())
                .name("&7Who is &e" + pp.getName() + "&7?")
                .lore(lore);

        addButton(new AutoGUIButton(skull));

        Icon block1 = null;
        Icon block2 = null;

        switch (pp.getHouse()) {
            case UNSORTED:
                block1 = Icon.start(Material.GRAY_STAINED_GLASS).name("&7Student");
                block2 = Icon.start(Material.WHITE_STAINED_GLASS).name("&fStudent");
                break;
            case GRYFFINDOR:
                block1 = Icon.start(Material.RED_WOOL).name("&7House&8: &4Gryffindor");
                block2 = Icon.start(Material.GOLD_BLOCK).name("&7House&8: &6Gryffindor");
                break;
            case SLYTHERIN:
                block1 = Icon.start(Material.GREEN_WOOL).name("&7House&8: &2Slytherin");
                block2 = Icon.start(Material.IRON_BLOCK).name("&7House&8: &7Slytherin");
                break;
            case RAVENCLAW:
                block1 = Icon.start(Material.BLUE_WOOL).name("&7House&8: &9Ravenclaw");
                block2 = Icon.start(Material.STONE).name("&7House&8: &7Ravenclaw");
                break;
            case HUFFLEPUFF:
                block1 = Icon.start(Material.YELLOW_WOOL).name("&7House&8: &eHufflepuff");
                block2 = Icon.start(Material.OBSIDIAN).name("&7House&8: &8Hufflepuff");
                break;
        }

        AutoGUIButton house1 = new AutoGUIButton(block1);
        AutoGUIButton house2 = new AutoGUIButton(block2);

        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);

        Role r = pp.getRole();
        Icon i = Icon.start(
                pp.isStudent() ?
                        Material.IRON_BLOCK :
                        Material.PURPLE_WOOL
        );

        i.name("&7Role&8: " + r.getColoredName());

        AutoGUIButton roleIndicator = new AutoGUIButton(i);

        setButton(9, roleIndicator);
        setButton(17, roleIndicator);
    }
}
