package model.db;

import model.base.BaseModel;

public class User extends BaseModel {
    /**
     * user name
     */
    final private String username;

    /**
     * Constructor
     * @param id - long id of user
     * @param username - name of user
     */
    public User(long id, String username) {
        super(id);
        this.username = username;
    }

    /**
     * get username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * toString method
     * @return the name of the user
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * to be overridden by subclasses that have non-String fields to validate
     */
    @Override
    protected void customValidate() {
    }
}
