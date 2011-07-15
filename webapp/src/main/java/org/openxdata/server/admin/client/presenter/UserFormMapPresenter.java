package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.controller.facade.MainViewControllerFacade;
import org.openxdata.server.admin.client.service.StudyManagerServiceAsync;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.StudyDef;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.mapping.UserFormMap;

/**
 *
 * @author kay
 */
public class UserFormMapPresenter extends ExtendedBaseMapPresenter<UserFormMap, User, FormDef> {

    public interface Display extends BaseDisplay<FormDef> {
    }
    private StudyManagerServiceAsync studyService;

    @Inject
    public UserFormMapPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, User.class, FormDef.class, UserFormMap.class);
        studyService = StudyManagerServiceAsync.Util.getInstance();
        bindHandler();
    }

    private void bindHandler() {
        EditableEvent.addHandler(eventBus, new EditableEvent.HandlerAdaptor<StudyDef>() {

            @Override
            public void onLoaded(List<StudyDef> items) {
                loadFormsFromStudy(items);
            }
        }).forClass(StudyDef.class);
    }

    @Override
    protected boolean isMappedToItem(User user, FormDef studyDef) {
        List<UserFormMap> userMappedForms = permissionResolver.getUserMappedForms(user, maps);
        if (user.hasAdministrativePrivileges()) return true;
        if (userMappedForms.isEmpty()) return false;
        for (UserFormMap userFormMap : userMappedForms) {
            if (userFormMap.getFormId() == studyDef.getId()) return true;
        }
        return false;
    }

    @Override
	protected UserFormMap createNewMap(User user, FormDef form) {
        UserFormMap map = new UserFormMap();
        map.setUserId(user.getId());
        map.setFormId(form.getId());
        return map;
    }

    @Override
    protected void onMapSaveComplete(List<? extends Editable> studiesAdded) {
        if (Utilities.hasNewItems(studiesAdded)) {
            MainViewControllerFacade.loadAllUserMappedForms(true);
        }
    }

    @Override
    protected void persistDelete(UserFormMap userStudyMap, SaveAsyncCallback saveAsync) {
        studyService.deleteUserMappedForm(userStudyMap, saveAsync);
    }

    @Override
    protected void persistSave(UserFormMap userStudyMap, SaveAsyncCallback saveAsync) {
        studyService.saveUserMappedForm(userStudyMap, saveAsync);
    }

    @Override
    public UserFormMap searchMap(int userId, int studyId, List<UserFormMap> userStudyMaps) {
        for (UserFormMap userFormMap : userStudyMaps) {
            if (userId == userFormMap.getUserId() && studyId == userFormMap.getFormId())
                return userFormMap;
        }
        return null;
    }

    private void loadFormsFromStudy(List<StudyDef> items) {
        List<FormDef> forms = new ArrayList<FormDef>();
        for (StudyDef studyDef : items) {
            forms.addAll(studyDef.getForms());
        }
        setSystemItemsToMap(forms);
    }
}
