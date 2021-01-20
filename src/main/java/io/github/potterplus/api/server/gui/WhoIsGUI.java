package io.github.potterplus.api.server.gui;

import io.github.potterplus.api.gui.GUI;
import io.github.potterplus.api.gui.button.AutoGUIButton;
import io.github.potterplus.api.misc.ItemStackBuilder;
import io.github.potterplus.api.misc.TimeConverter;
import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.server.player.PotterPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WhoIsGUI extends GUI {

    public WhoIsGUI(PotterPlusAPI api, Player player) {
        super("&8Who is &e" + player.getName() + "&8?", 36);

        PotterPlayer pp = new PotterPlayer(player.getUniqueId(), api);
        String house = "&7House&8: &r" + pp.getHouse().getColoredName();
        String role = "&7Role&8: &r" + pp.getRole().getColoredName();
        String playingSince = "&7Playing since&8: &e" + new SimpleDateFormat(TimeConverter.PATTERN)
                .format(new Date(pp.getPlayingSince()));

        ItemStackBuilder skull = ItemStackBuilder
                .skull(player.getUniqueId())
                .name("&7Who is &e" + player.getName() + "&7?")
                .lore(
                        house,
                        role,
                        "",
                        playingSince
                );

        addButton(new AutoGUIButton(skull));

        ItemStackBuilder block1 = null;
        ItemStackBuilder block2 = null;

        switch (pp.getHouse()) {
            case UNSORTED:
                block1 = ItemStackBuilder.start(Material.GRAY_STAINED_GLASS).name("&7Student");
                block2 = ItemStackBuilder.start(Material.WHITE_STAINED_GLASS).name("&fStudent");
                break;
            case GRYFFINDOR:
                block1 = ItemStackBuilder.start(Material.RED_WOOL).name("&4Gryffindor");
                block2 = ItemStackBuilder.start(Material.GOLD_BLOCK).name("&6Gryffindor");
                break;
            case SLYTHERIN:
                block1 = ItemStackBuilder.start(Material.GREEN_WOOL).name("&2Slytherin");
                block2 = ItemStackBuilder.start(Material.IRON_BLOCK).name("&7Slytherin");
                break;
            case RAVENCLAW:
                block1 = ItemStackBuilder.start(Material.BLUE_WOOL).name("&9Ravenclaw");
                block2 = ItemStackBuilder.start(Material.STONE).name("&7Ravenclaw");
                break;
            case HUFFLEPUFF:
                block1 = ItemStackBuilder.start(Material.YELLOW_WOOL).name("&eHufflepuff");
                block2 = ItemStackBuilder.start(Material.OBSIDIAN).name("&8Hufflepuff");
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
    }
}
