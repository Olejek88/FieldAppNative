package ru.shtrm.fieldappnative.db.realm;

import java.util.Date;

public interface IDbObject {
    String getUuid();

    String getImageFile();

    String getImageFilePath();

    String getImageFileUrl(String userName);

    Date getCreatedAt();

    Date getChangedAt();
}
