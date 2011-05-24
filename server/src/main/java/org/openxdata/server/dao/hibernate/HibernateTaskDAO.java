package org.openxdata.server.dao.hibernate;

import java.util.List;

import org.openxdata.server.admin.model.TaskDef;
import org.openxdata.server.dao.TaskDAO;
import org.springframework.stereotype.Repository;

import com.googlecode.genericdao.search.Search;

/**
 * Provides a hibernate implementation
 * of the <code>TaskDAO</code> data access <code> interface.</code>
 * 
 *
 */
@Repository("taskDAO")
public class HibernateTaskDAO extends BaseDAOImpl<TaskDef> implements TaskDAO {
	
	@Override
	public void deleteTask(TaskDef task) {
		remove(task);
	}

	@Override
	public List<TaskDef> getTasks() {
		return findAll();
	}

	@Override
	public void saveTask(TaskDef task) {
		save(task);
	}
	
	@Override
	public TaskDef getTask(String taskName) {	    
        return searchUnique(new Search().addFilterEqual("name", taskName));
	}
}
