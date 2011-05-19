package org.openxdata.client.util;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Controller;
import java.util.List;
import org.openxdata.client.Emit;
import org.openxdata.client.controllers.EditStudyFormController;
import org.openxdata.client.controllers.NewStudyFormController;
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

    public UsermapUtilities(Controller controller, List<UserStudyMap> mappedStudies) {
        this.controller = controller;
        this.mappedStudies = mappedStudies;
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
