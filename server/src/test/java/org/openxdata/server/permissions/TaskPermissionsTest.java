package org.openxdata.server.permissions;

import org.junit.Test;
import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.admin.model.exception.OpenXDataException;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;


/**
 * Tests permissions for accessing tasks.
 * 
 * @author daniel
 *
 */
public class TaskPermissionsTest extends PermissionsTest {
   
    @Test(expected=OpenXDataSecurityException.class)
    public void saveTask_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
		taskService.saveTask(new TaskDef());
    }
    
    @Test(expected=OpenXDataSecurityException.class)
    public void deleteTask_shouldThrowOpenXDataSecurityException() throws OpenXDataException {
    	taskService.deleteTask(new TaskDef());
    }
}
