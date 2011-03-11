package org.openxdata.server.admin.client.presenter;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.Inject;
import org.openxdata.server.admin.client.view.BasePropertyDisplay;
import org.openxdata.server.admin.client.view.ScheduleView;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.client.view.event.ItemSelectedEvent;
import org.openxdata.server.admin.client.view.event.PresenterChangeEvent;
import org.openxdata.server.admin.client.view.event.ViewEvent;
import org.openxdata.server.admin.model.Permission;
import org.openxdata.server.admin.model.TaskDef;

/**
 *
 * @author kay
 */
public class TaskPresenter implements IPresenter<TaskPresenter.Display> {

    public interface Display extends BasePropertyDisplay.Interface {

        public HasText getTxtName();

        public HasText getTxtDescription();

        public HasText getTxtClass();

        public HasText getTxtStarted();

        public void setTaskDef(TaskDef taskDef);
    }
    private TaskPresenter thisPresenter = this;
    private ScheduleView scheduleView;
    private Display display;
    private EventBus eventBus;
    private TaskDef taskDef;

    @Inject
    public TaskPresenter(Display display, EventBus eventBus, ScheduleView scheduleView) {
        this.display = display;
        this.eventBus = eventBus;
        this.scheduleView = scheduleView;
        if (permissionResolver.isViewPermission(Permission.PERM_VIEW_TASKS)) {
            if (permissionResolver.isEditPermission(Permission.PERM_EDIT_TASKS))
                bindUI();
            else
                display.disableAll();
        } else {
            display.showNoPermissionView();
        }
        bindHandlers();

    }

    private void bindUI() {
        ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                syncChange();
            }
        };
        display.addGeneralChangeHadler(valueChangeHandler);

        if (permissionResolver.isPermission(Permission.PERM_TASK_SCHEDULING))
            display.addTab(scheduleView, CONSTANTS.label_schedule());
    }

    private void bindHandlers() {
        ItemSelectedEvent.addHandler(eventBus, new ItemSelectedEvent.Handler<TaskDef>() {

            @Override
            public void onSelected(Composite sender, TaskDef item) {
                setTaskDef(item);
            }
        }).forClass(TaskDef.class);

        ViewEvent.addHandler(eventBus, new ViewEvent.Handler<TaskDef>() {

            @Override
            public void onView() {
                eventBus.fireEvent(new PresenterChangeEvent(thisPresenter));
            }
        }).forClass(TaskDef.class);
    }

    private void syncChange() {
        taskDef.setName(display.getTxtName().getText());
        taskDef.setDescription(display.getTxtDescription().getText());
        taskDef.setTaskClass(display.getTxtClass().getText());
        eventBus.fireEvent(new EditableEvent<TaskDef>(taskDef));
    }

    @Override
    public Display getDisplay() {
        return display;
    }

    public void setTaskDef(TaskDef taskDef) {
        display.setTaskDef(taskDef);
        this.taskDef = taskDef;
    }
}
