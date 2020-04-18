package ru.shtrm.fieldappnative;

import ru.shtrm.fieldappnative.db.realm.User;

public class AuthorizedUser {

    private static AuthorizedUser mInstance;
    private User mUser;
    private String mUuid;
    private String mTagId;
    private String mToken;
    private String mLogin;

    public static synchronized AuthorizedUser getInstance() {
        if (mInstance == null) {
            mInstance = new AuthorizedUser();
        }
        return mInstance;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    /**
     * @return the mUuid
     */
    public String getUuid() {
        return mUuid;
    }

    /**
     * @param uuid the mUuid to set
     */
    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    /**
     * @return the mTagId
     */
    public String getTagId() {
        return mTagId;
    }

    /**
     * @param tagId the mTagId to set
     */
    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    /**
     * @return the mToken
     */
    public String getToken() {
        return mToken;
    }

    /**
     * @param token the mToken to set
     */
    public void setToken(String token) {
        mToken = token;
    }

    /**
     * @return The bearer
     */
    public String getBearer() {
        return "bearer " + mToken;
    }

    /**
     * @return the Uuid
     */
    public User getUser() {
        return mUser;
    }

    /**
     * @param user User
     */
    public void setUser(User user) {
        mUser = user;
    }


    /**
     * Обнуляем информацию о текущем пользователе.
     */
    public void reset() {
        mLogin = null;
        mTagId = null;
        mToken = null;
        mUuid = null;
    }
}
