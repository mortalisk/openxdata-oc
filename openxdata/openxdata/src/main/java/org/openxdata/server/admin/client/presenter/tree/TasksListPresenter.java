package org.openxdata.server.admin.client.presenter.tree;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import java.util.List;
import org.openxdata.server.admin.client.controller.callback.OpenXDataAsyncCallback;
import org.openxdata.server.admin.client.controller.callback.SaveAsyncCallback;
import org.openxdata.server.admin.client.locale.OpenXdataText;
import org.openxdata.server.admin.client.locale.TextConstants;
import org.openxdata.server.admin.client.presenter.WidgetDisplay;
import org.openxdata.server.admin.client.service.TaskServiceAsync;
import org.openxdata.server.admin.client.util.MainViewControllerUtil;
import org.openxdata.server.admin.client.util.Utilities;
import org.openxdata.server.admin.client.view.event.EditableEvent;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.sharedlib.client.util.FormUtil;

/**
 *
 * @author kay
 */
public class TasksListPresenter extends BaseTreePresenter<TaskDef, TasksListPresenter.Display> {

    public interface Display extends IBaseTreeDisplay<TaskDef> {

        public static String diplayName = "Tasks";
    }
    private TaskServiceAsync taskService;

    @Inject
    public TasksListPresenter(EventBus eventBus, Display display) {
        super(display, eventBus, TaskDef.class);
        taskService = TaskServiceAsync.Util.getInstance();
        bindUI();
    }

    private void bindUI() {
        display.addCommand(CONSTANTS.label_start(), new Command() {

            @Override
            public void execute() {
                stopTask();
            }
        }, WidgetDisplay.images.play());

        display.addCommand(CONSTANTS.label_stop(), new Command() {

            @Override
            public void execute() {
                startTask();
            }
        }, WidgetDisplay.images.stop());
    }

    private void startTask() {
        if (!Window.confirm("Cofirm Stop?")) return;
        final TaskDef selected = display.getSelected();
        Utilities.showProgress("Stopping...");
        taskService.stopTask(selected, new OpenXDataAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (!result)
                    Utilities.displayMessage("Failed to Stop Task");
                selected.setRunning(result);
                FormUtil.dlg.hide();
            }
        });
    }

    private void stopTask() {
        if (!Window.confirm("Cofirm Start?")) return;
        final TaskDef selected = display.getSelected();
        Utilities.showProgress("Starting...");
        taskService.startTask(selected, new OpenXDataAsyncCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                if (!result)
                    Utilities.displayMessage("Failed To Start Task");
                selected.setRunning(result);
                FormUtil.dlg.hide();
            }
        });
    }

    @Override
    protected TaskDef getNewItem() {
        TaskDef task = new TaskDef(CONSTANTS.label_new_task());
        task.setStartOnStartup(true);
        return task;
    }

    @Override
    protected boolean canView() {
        return permissionResolver.isViewPermission("Perm_View_Tasks");
    }

    @Override
    protected boolean canDelete() {
        return permissionResolver.isDeleteTasks();
    }

    @Override
    protected boolean canAdd() {
        return permissionResolver.isAddTasks();
    }

    @Override
    protected void loadItems() {
        Utilities.showProgress(OpenXdataText.get(TextConstants.LOADING_ROLES));
        OpenXDataAsyncCallback<List<TaskDef>> callback = new OpenXDataAsyncCallback<List<TaskDef>>() {

            @Override
            public void onSuccess(List<TaskDef> result) {
                setItems(result);
                eventBus.fireEvent(new EditableEvent<TaskDef>(result, TaskDef.class));
                FormUtil.dlg.hide();
            }
        };
        taskService.getTasks(callback);
    }

    @Override
    protected String getFailedMessage() {
        return "Error Occured Saving Tasks";
    }

    @Override
    protected boolean saveDirtyItems(List<TaskDef> dirtyItems, SaveAsyncCallback callback) {
        for (TaskDef taskDef : dirtyItems) {
            callback.setCurrentItem(taskDef);
            MainViewControllerUtil.setEditableProperties(taskDef);
            taskService.saveTask(taskDef, callback);
        }
        return true;
    }

    @Override
    protected void persistDelete(List<TaskDef> deletedItems, SaveAsyncCallback callback) {
        for (TaskDef taskDef : deletedItems) {
            callback.setCurrentItem(taskDef);
            taskService.deleteTask(taskDef, callback);
        }
    }

    @Override
    protected String getSuccessMessage() {
        return OpenXdataText.get(TextConstants.TASKS_SAVED_SUCCESSFULLY);
    }
}
