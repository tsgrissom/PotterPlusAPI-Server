package io.github.potterplus.api.server.storage;

import io.github.potterplus.api.storage.database.DatabaseController;
import lombok.Getter;
import lombok.NonNull;
import io.github.potterplus.api.server.PotterPlusAPI;

public class PotterPlusDBController extends DatabaseController {

    @NonNull @Getter
    private PotterPlusAPI api;

    public PotterPlusDBController(PotterPlusAPI api) {
        super(api.getConfig().getString("database.host"), api.getConfig().getString("database.db"), api.getConfig().getString("database.user"), api.getConfig().getString("database.pass"));
        this.api = api;
    }
}
