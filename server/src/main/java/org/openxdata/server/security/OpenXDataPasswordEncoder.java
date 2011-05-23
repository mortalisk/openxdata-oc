package org.openxdata.server.security;

import org.openxdata.server.security.util.OpenXDataSecurityUtil;
import org.springframework.security.providers.encoding.PasswordEncoder;

/**
 * Converts the OpenXData SecurityUtil so it can be
 * used in Spring Security
 * 
 * @author dagmar@cell-life.org.za
 */
public class OpenXDataPasswordEncoder implements PasswordEncoder {

    @Override
	public String encodePassword(String password, Object salt) {
        return OpenXDataSecurityUtil.encodeString(password + salt);
    }

    @Override
	public boolean isPasswordValid(String encodedPassword, String rawPassword, Object salt) {
        String encoded = encodePassword(rawPassword, salt);
        if (encoded.equals(encodedPassword)) {
            return true;
        } else {
        	//Just incase we still have passwords hashed with buggy version of encodeString 
        	encoded = OpenXDataSecurityUtil.encodeString2(rawPassword + salt);
            return encoded.equals(encodedPassword);
        }
    }
}