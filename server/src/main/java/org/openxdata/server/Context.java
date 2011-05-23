package org.openxdata.server;

import org.openxdata.server.admin.model.User;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.security.OpenXdataUserDetailsService;
import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Server application context.
 * @author daniel
 * @author Angel
 *
 */
@Transactional
public class Context {		

	private static UserDetailsService userDetailsService;

	public Context() { }
	
	public static void setAuthenticatedUser(User user) {
		OpenXDataUserDetails userDetails = ((OpenXdataUserDetailsService) userDetailsService).getUserDetailsForUser(user);
		if (userDetails != null){
			OpenXDataSecurityUtil.setSecurityContext(userDetails);
		}
	}
	
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		Context.userDetailsService = userDetailsService;
	}
}
