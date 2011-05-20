package org.openxdata.client.util;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.client.Emit;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.controllers.NewStudyFormController;
import org.openxdata.client.model.UserSummary;
import org.openxdata.client.views.UserAccessGrids;
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
    public void setUserStudyMap(UserAccessGrids grid, StudyDef study, List<User> users) {
        grid.clear();

        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        for (User u : users) {
            // check whether user is mapped to this study
            boolean found = false;
            for (UserStudyMap map : mappedStudies) {
                if ((map.getUserId() == u.getId())
                        && (map.getStudyId() == study.getStudyId())) {
                    grid.addMappedUser(new UserSummary(u));
                    mappedUsers.add(new UserSummary(u));
                    found = true;
                    break;
                }
            }
            if (!found) {
                grid.addUnmappedUser(new UserSummary(u));
                unMappedUsers.add(new UserSummary(u));
            }
        }
        grid.updateLists(unMappedUsers, mappedUsers);
    }

    /*
     * Load formdefinition names into left and right listboxes appropriately
     */
    public void setUserFormMap(UserAccessGrids grid, FormDef form, List<User> users) {
        grid.clear();

        List<UserSummary> mappedUsers = new ArrayList<UserSummary>();
        List<UserSummary> unMappedUsers = new ArrayList<UserSummary>();
        for (User user : users) {
            boolean found = false;
            for (UserFormMap map : mappedForms) {
                if ((map.getUserId() == user.getId())
                        && (map.getFormId() == form.getFormId())) {
                    grid.addMappedUser(new UserSummary(user));
                    mappedUsers.add(new UserSummary(user));
                    found = true;
                    break;
                }
            }
            if (!found) {
                grid.addUnmappedUser(new UserSummary(user));
                unMappedUsers.add(new UserSummary(user));
            }
        }
        grid.updateLists(unMappedUsers, mappedUsers);
    }


    public void saveUserStudyMap(UserAccessGrids grid, StudyDef study, List<User> users) {
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

    private void deleteUserMappedStudy(UserAccessGrids grid, List<User> users) {
        for (User unmappedUser : grid.getTempItemstoUnmap()) {
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

    public void saveUserFormMap(UserAccessGrids grid, FormDef form, List<User> users, List<UserFormMap> mappedForms) {
        if (!grid.getTempMappedItems().isEmpty()) {
            for (int i = 0; i < grid.getTempMappedItems().size(); ++i) {
                for (User user : users) {
                    if (user.getName().equals(
                            grid.getTempMappedItems().get(i).getName())
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
        if (!grid.getTempItemstoUnmap().isEmpty()) {
            for (int i = 0; i < grid.getTempItemstoUnmap().size(); ++i) {
                for (UserFormMap map : mappedForms) {
                    for (User user : users) {
                        if ((user.getName().equals(grid.getTempItemstoUnmap().get(i).getName()))
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
