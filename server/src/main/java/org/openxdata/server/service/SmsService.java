package org.openxdata.server.service;

import java.util.List;

import org.openxdata.server.admin.model.FormSmsArchive;
import org.smslib.OutboundMessage;

public interface SmsService {
	/**
	 * Gets a list of FormSmsArchives
	 * @return the list of FormSmsArchves
	 */
	List<FormSmsArchive> getFormSmsArchives();

    void sendMessage(OutboundMessage next);
}
