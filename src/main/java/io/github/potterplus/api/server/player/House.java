package io.github.potterplus.api.server.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

@RequiredArgsConstructor
public enum House {

    GRYFFINDOR(ChatColor.DARK_RED, ChatColor.GOLD, "&8(&4G&8)&r", "&8(&4G&8)&4", "group.gryffindor"),

    SLYTHERIN(ChatColor.DARK_GREEN, ChatColor.DARK_GRAY, "&8(&2S&8)&r", "&8(&2S&8)&2", "group.slytherin"),

    RAVENCLAW(ChatColor.BLUE, ChatColor.GRAY, "&7(&9R&7)&r", "&7(&9R&7)&2", "group.ravenclaw"),

    HUFFLEPUFF(ChatColor.YELLOW, ChatColor.DARK_GRAY, "&8(&eH&8)&r", "&8(&eH&8)&2", "group.hufflepuff"),

    UNSORTED(ChatColor.GRAY, ChatColor.GRAY, "&r", "&7", "group.default");

    @NonNull @Getter
    ChatColor primaryColor, secondaryColor;

    @NonNull @Getter
    String prefixReset, prefixColored, permissionNode;

    public String getName() {
        String s = this.name();

        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public String getColoredName() {
        return getPrimaryColor() + getName();
    }

    public Permission getPermission() {
        return new Permission(this.getPermissionNode());
    }
}
