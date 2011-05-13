package org.openxdata.server.security;

import org.openxdata.server.admin.model.User;
import org.springframework.security.userdetails.UserDetailsService;

public interface OpenXdataUserDetailsService extends UserDetailsService {

	OpenXDataUserDetails getUserDetailsForUser(User user);

}
