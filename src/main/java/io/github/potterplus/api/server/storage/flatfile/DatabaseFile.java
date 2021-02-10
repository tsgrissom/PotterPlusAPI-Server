package io.github.potterplus.api.server.storage.flatfile;

import io.github.potterplus.api.server.PotterPlusAPI;
import io.github.potterplus.api.storage.flatfile.PluginYamlFile;

public class DatabaseFile extends PluginYamlFile<PotterPlusAPI> {

    public DatabaseFile(PotterPlusAPI plugin) {
        super(plugin, "db.yml");
    }

    public String getHost() {
        return getFileConfiguration().getString("host");
    }

    public String getDatabase() {
        return getFileConfiguration().getString("db");
    }

    public String getUsername() {
        return getFileConfiguration().getString("user");
    }

    public String getPassword() {
        return getFileConfiguration().getString("pass");
    }
}
