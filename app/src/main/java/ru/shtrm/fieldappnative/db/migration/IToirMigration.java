package ru.shtrm.fieldappnative.db.migration;

import io.realm.DynamicRealm;

public interface IToirMigration {
    void migration(DynamicRealm realm);
}
