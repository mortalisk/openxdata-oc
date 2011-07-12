package org.openxdata.client.util;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.client.Emit;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.views.UserAccessListField;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;
import org.openxdata.server.admin.model.mapping.UserStudyMap;

/**
 * Encapsulates utility functions used by the wizardview.
 *
 */
public class UsermapUtilities {

    private Controller controller;
    private List<UserStudyMap> mappedStudies;
    private List<UserFormMap> mappedForms;

    public UsermapUtilities(Controller controller) {
        this.controller = controller;
    }
    
	public void setUserMappedStudies(List<UserStudyMap> amappedStudies) {
		this.mappedStudies = amappedStudies;
	}

	public void setUserMappedForms(List<UserFormMap> amappedForms) {
		this.mappedForms = amappedForms;
	}
    
    public List<UserStudyMap> getUserMappedStudies(){
        return this.mappedStudies;
    }
    
    public List<UserFormMap> getUserMappedForms(){
        return this.mappedForms;
    }

    /*
     * Load study names into left and right listboxes appropriately
     */
    public void setUserStudyMap(UserAccessListField list, StudyDef study, List<User> users) {
        list.clear();

        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        for (User u : users) {
            // check whether user is mapped to this study
            boolean found = false;
            for (UserStudyMap map : mappedStudies) {
                if ((map.getUserId() == u.getId())
                        && (map.getStudyId() == study.getStudyId())) {
                    list.addMappedUser(new UserSummary(u));
                    mappedUsers.add(new UserSummary(u));
                    found = true;
                    break;
                }
            }
            if (!found) {
                list.addUnmappedUser(new UserSummary(u));
                unMappedUsers.add(new UserSummary(u));
            }
        }
        list.updateLists(unMappedUsers, mappedUsers);
    }

    /*
     * Load Form Definition names into left and right List Boxes appropriately
     */
    public void setUserFormMap(UserAccessListField list, FormDef form, List<User> users) {
        list.clear();

        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        for (User user : users) {
            boolean found = false;
            for (UserFormMap map : mappedForms) {
                if ((map.getUserId() == user.getId())
                        && (map.getFormId() == form.getFormId())) {
                    list.addMappedUser(new UserSummary(user));
                    mappedUsers.add(new UserSummary(user));
                    found = true;
                    break;
                }
            }
            if (!found) {
                list.addUnmappedUser(new UserSummary(user));
                unMappedUsers.add(new UserSummary(user));
            }
        }
        list.updateLists(unMappedUsers, mappedUsers);
    }


    public void saveUserStudyMap(UserAccessListField grid, StudyDef study, List<User> users) {
        String loggedInUsername = ((User) Registry.get(Emit.LOGGED_IN_USER_NAME)).getName();
        for (User mappedUser : grid.getTempMappedItems()) {
            for (User user : users) {
                if (user.getName().equals(mappedUser.getName())
                        && !user.getName().equals(loggedInUsername)) {
                    // check already mapped users to this study
                    UserStudyMap map = new UserStudyMap();
                    map.addStudy(study);
                    map.addUser(user);
                    map.setDirty(true);
                    if (controller instanceof EditStudyFormController) {
                        ((EditStudyFormController) controller).saveUserMappedStudy(map);
                    } else if (controller instanceof NewStudyFormController) {
                        ((NewStudyFormController) controller).saveUserMappedStudy(map);
                    }
                    break;
                }
            }
        }
        deleteUserMappedStudy(grid, users);
    }

    private void deleteUserMappedStudy(UserAccessListField list, List<User> users) {
        for (User unmappedUser : list.getTempItemstoUnmap()) {
            for (UserStudyMap map : mappedStudies) {
                for (User user : users) {
                    if (user.getName().equals(unmappedUser.getName())
                            && (user.getUserId() == map.getUserId())) {
                        if (controller instanceof EditStudyFormController) {
                            ((EditStudyFormController) controller).deleteUserMappedStudy(map);
                        } else if (controller instanceof NewStudyFormController) {
                            ((NewStudyFormController) controller).deleteUserMappedStudy(map);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void saveUserFormMap(UserAccessListField list, FormDef form, List<User> users, List<UserFormMap> mappedForms) {
        if (!list.getTempMappedItems().isEmpty()) {
            for (int i = 0; i < list.getTempMappedItems().size(); ++i) {
                for (User user : users) {
                    if (user.getName().equals(
                            list.getTempMappedItems().get(i).getName())
                            && !(user.getName().equals(((User) Registry.get(Emit.LOGGED_IN_USER_NAME)).getName()))) {
                        UserFormMap map = new UserFormMap();
                        map.addForm(form);
                        map.addUser(user);
                        map.setDirty(true);
                        if (controller instanceof EditStudyFormController) {
                            ((EditStudyFormController) controller).saveUserMappedForm(map);
                        } else if (controller instanceof NewStudyFormController) {
                            ((NewStudyFormController) controller).saveUserMappedForm(map);
                        }
                        break;
                    }
                }
            }
        }
        if (!list.getTempItemstoUnmap().isEmpty()) {
            for (int i = 0; i < list.getTempItemstoUnmap().size(); ++i) {
                for (UserFormMap map : mappedForms) {
                    for (User user : users) {
                        if ((user.getName().equals(list.getTempItemstoUnmap().get(i).getName()))
                                && (user.getUserId() == map.getUserId())) {
                            if (controller instanceof EditStudyFormController) {
                                ((EditStudyFormController) controller).deleteUserMappedForm(map);
                            } else if (controller instanceof NewStudyFormController) {
                                ((NewStudyFormController) controller).deleteUserMappedForm(map);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
