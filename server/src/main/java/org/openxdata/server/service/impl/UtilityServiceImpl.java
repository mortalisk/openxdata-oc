package org.openxdata.server.service.impl;

import java.util.List;

import org.openxdata.server.service.UtilityService;
import org.openxdata.server.sms.WapPushSms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation for <code>Utility Service</code>.
 * @author Angel
 *
 */
@Transactional
@Service("utilityService")
public class UtilityServiceImpl implements UtilityService {

	public UtilityServiceImpl() {}
	
    @Override
    // FIXME: security?
    public Boolean installMobileApp(List<String> phonenos, String url,
    		String modemComPort, int modemBaudRate, String promptText) {
    	
    	return WapPushSms.sendMessages(phonenos, url, modemComPort, modemBaudRate, promptText);
    }

}
