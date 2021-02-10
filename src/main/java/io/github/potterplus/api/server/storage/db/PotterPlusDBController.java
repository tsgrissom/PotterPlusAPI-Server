package io.github.potterplus.api.server.storage.db;

import io.github.potterplus.api.storage.database.DatabaseController;
import lombok.Getter;
import lombok.NonNull;
import io.github.potterplus.api.server.PotterPlusAPI;

public class PotterPlusDBController extends DatabaseController {

    @NonNull @Getter
    private final PotterPlusAPI api;

    public PotterPlusDBController(PotterPlusAPI api) {
        super(api.getDatabaseFile().getHost(), api.getDatabaseFile().getDatabase(), api.getDatabaseFile().getUsername(), api.getDatabaseFile().getPassword());

        this.api = api;
    }
}
