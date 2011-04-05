package org.openxdata.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import org.openxdata.server.admin.model.User;

/**
 *
 * @author victor
 */
public class UserSummary extends BaseModel {

    private User user;

    public UserSummary() {
    }

    public UserSummary(User user) {
        setUser(user);
    }

    public UserSummary(String id, String userName) {
        setId(id);
        setName(userName);
    }

    public void setId(String id) {
        set("id", id);
    }

    public String getId() {
        return get("id");
    }

    public void setName(String userName) {
        set("name", userName);
    }

    public String getName() {
        return get("name");
    }

    public void setUser(User user) {
        this.user = user;
        updateUser(user);
    }

    public User getUser() {
        return user;
    }

    public void updateUser(User user) {
        setId(String.valueOf(user.getId()));
        setName(user.getName());
    }
}
