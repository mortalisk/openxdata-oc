package org.openxdata.server.admin.client.service;

import java.util.List;

import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.exception.OpenXDataSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Defines the client side contract for the SMS Service.
 */
public interface SmsService extends RemoteService{

	/**
	 * Fetches <tt>Form SMS Archives.</tt>
	 * 
	 * @return <tt>List</tt> of <tt>Form SMS Archives.</tt>
	 * @throws OpenXDataSecurityException For any <tt>security related</tt> that occurs on the <tt>service layer.</tt>
	 */
	List<FormSmsArchive> getFormSmsArchives() throws OpenXDataSecurityException;
}
