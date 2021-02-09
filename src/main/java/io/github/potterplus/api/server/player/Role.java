package io.github.potterplus.api.server.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.permissions.Permission;

@RequiredArgsConstructor
public enum Role {

    HEAD(ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE, "group.admin"),

    PREFECT(ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE, "group.mod"),

    STUDENT(ChatColor.GRAY, ChatColor.DARK_GRAY, "group.default");

    @NonNull @Getter
    ChatColor primaryColor, secondaryColor;

    @NonNull @Getter
    String permissionNode;

    public String getName() {
        String s = this.name();

        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public String getColorPrefix() {
        return primaryColor.toString() + "";
    }

    public String getColoredName() {
        return getPrimaryColor() + getName();
    }

    public Permission getPermission() {
        return new Permission(this.getPermissionNode());
    }
}
