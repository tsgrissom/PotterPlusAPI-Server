package io.github.potterplus.api.server.gui;

import io.github.potterplus.api.item.Icon;
import io.github.potterplus.api.misc.TimeUtilities;
import io.github.potterplus.api.server.player.PotterPlayer;
import io.github.potterplus.api.server.player.Role;
import io.github.potterplus.api.ui.UserInterface;
import io.github.potterplus.api.ui.button.AutoUIButton;
import io.github.potterplus.api.ui.button.UIButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoIsGUI extends UserInterface {

    public WhoIsGUI(PotterPlayer target, PotterPlayer dispatcher) {
        this(target, dispatcher, false);
    }

    public WhoIsGUI(PotterPlayer target, PotterPlayer dispatcher, boolean forceUserView) {
        super("&8Who is &e" + target.getName() + "&8?", forceUserView || !dispatcher.isStaff() ? 18 : 36);

        boolean staff = dispatcher.isStaff();

        List<String> lore = new ArrayList<>();

        lore.add("&7House&8: &r" + target.getHouse().getColoredName());
        lore.add("&7Role&8: &r" + target.getRole().getColoredName());

        if (target.isOnline()) {
            Player p = target.getPlayer();

            lore.add("");
            lore.add("&7Display name&8: &r" + p.getDisplayName());
        }

        lore.add("");
        lore.add("&7Playing since&8: &e" + TimeUtilities.prettyTime(target.getPlayingSince()));

        if (staff) {
            lore.add("");
            lore.add("&7UUID&8: &e" + target.getUniqueStr());
        }

        Icon skull = Icon
                .skull(target.getUniqueId())
                .name("&7Who is &e" + target.getName() + "&7?")
                .lore(lore);

        addButton(new AutoUIButton(skull));

        Icon block1 = null;
        Icon block2 = null;

        switch (target.getHouse()) {
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

        AutoUIButton house1 = new AutoUIButton(block1);
        AutoUIButton house2 = new AutoUIButton(block2);

        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);
        addButton(house1);
        addButton(house2);

        Role r = target.getRole();
        Icon i = Icon.start(
                target.isStudent() ?
                        Material.IRON_BLOCK :
                        Material.PURPLE_WOOL
        );

        i.name("&7Role&8: " + r.getColoredName());

        AutoUIButton roleIndicator = new AutoUIButton(i);

        setButton(9, roleIndicator);
        setButton(17, roleIndicator);

        if (staff) {
            UIButton loginHistory = new UIButton(
                    Icon.start(Material.PAPER)
                            .name("&3View login history")
                            .lore("&7View this user's login history")
            );

            loginHistory.setListener((e -> {
                e.setCancelled(true);
                if (e.getWhoClicked() instanceof Player) {
                    ((Player) e.getWhoClicked()).performCommand("loginhistory " + target.getName());
                }
            }));

            UIButton viewAsUser = new UIButton(
                    Icon.start(Material.BARRIER)
                            .name("&3View as user")
                            .lore("&7You are currently viewing this user as a staff member")
            );

            viewAsUser.setListener((e -> {
                e.setCancelled(true);
                new WhoIsGUI(target, dispatcher, true);
            }));

            setButton(34, loginHistory);
            setButton(35, viewAsUser);
        }

        this.activate(dispatcher.getPlayer());
    }
}
