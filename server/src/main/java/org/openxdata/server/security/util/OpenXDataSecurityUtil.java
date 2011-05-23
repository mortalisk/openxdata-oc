package org.openxdata.server.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.OpenXDataSessionExpiredException;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.springframework.security.Authentication;
import org.springframework.security.concurrent.SessionRegistryUtils;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.context.SecurityContextImpl;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;


/**
 * This class has utilities used during the generation of 
 * a SHA-1 hash of a user's password.
 * 
 * @author daniel
 *
 */
public class OpenXDataSecurityUtil {

	 /** The logger. */
    private static Logger log = Logger.getLogger(OpenXDataSecurityUtil.class);


	/**
	 * This method will hash <code>strToEncode</code> using the preferred
	 * algorithm.  Currently, OpenXData's preferred algorithm is hard coded
	 * to be SHA-1.
	 *  
	 * @param strToEncode string to encode
	 * @return the SHA-1 encryption of a given string
     * @throws UnexpectedException if NoSuchAlgorithmException gets thrown.
	 */
	public static String encodeString(String strToEncode) {
		try{
			String algorithm = "SHA1";
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] input = strToEncode.getBytes(); //TODO: pick a specific character encoding, don't rely on the platform default
			return hexString(md.digest(input));
		}
		catch(NoSuchAlgorithmException ex){
            throw new UnexpectedException(ex);
		}
	}
	
	
	//TODO This method is here only for backward compatibility because
	//it calls the buggy hexString2
	public static String encodeString2(String strToEncode) {
		try{
			String algorithm = "SHA1";
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] input = strToEncode.getBytes(); //TODO: pick a specific character encoding, don't rely on the platform default
			return hexString2(md.digest(input));
		}
		catch(NoSuchAlgorithmException ex){
            throw new UnexpectedException(ex);
		}
	}

	
	//TODO This method is here only for backward compatibility because its buggy.
	/**
	 * Convenience method to convert a byte array to a string
	 * 
	 * @param b Byte array to convert to HexString
	 * @return Hexidecimal based string
	 */
	private static String hexString2(byte[] b) {
		if (b == null || b.length < 1)
			return "";
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			s.append(Integer.toHexString(b[i] & 0xFF));
		}
		return new String(s);
	}
	
	
	/**
	 * Convenience method to convert a byte array to a string.
	 * This solves a bug in the above method.
	 * 
	 * @param b
	 * @return
	 */
	private static String hexString(byte[] b) {
		StringBuffer buf = new StringBuffer();
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		int len = b.length;
		int high = 0;
		int low = 0;
		for (int i = 0; i < len; i++) {
			high = ((b[i] & 0xf0) >> 4);
			low = (b[i] & 0x0f);
			buf.append(hexChars[high]);
			buf.append(hexChars[low]);
		}
		
		return buf.toString();
	}

	/**
	 * Sets the <code>Spring security context</code> with the current <code>User</code> authentication details.
	 * 
	 * @param user - User to place in security context.
	 */
	public static void setSecurityContext(OpenXDataUserDetails userDetails) {
		setSecurityContext(userDetails, null);
	}

        public static void setSecurityContext(OpenXDataUserDetails userDetails, String sessionId) {
            User user = userDetails.getOXDUser();
                // Proceed to put the User in the spring security Context.
		SecurityContext sc = new SecurityContextImpl();
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), userDetails.getAuthorities());
                auth.setDetails(sessionId);
		sc.setAuthentication(auth);
		SecurityContextHolder.setContext(sc);
                log.info("Successfully logged in User: << " + user.getName() + " >> ");
		log.info("<< " + "Setting User:" + user.getName() + " in Context" + ">> ");
        }
	
    public static User getLoggedInUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof OpenXDataUserDetails) {
                OpenXDataUserDetails userDetails = (OpenXDataUserDetails) principal;
                return userDetails.getOXDUser();
            } else if (principal instanceof User) {
                return (User) principal;
            }
        }
        throw new OpenXDataSessionExpiredException("Could not find logged in user");
    }
	
	/**
	 * This method will generate a random string 
	 * 
	 * @return a secure random token.
	 */
	public static String getRandomToken() {
		Random rng = new Random();
		return encodeString(Long.toString(System.currentTimeMillis()) 
				+ Long.toString(rng.nextLong()));
	}

    public static String getCurrentSession() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return null;
        }
        Authentication authentication = context.getAuthentication();

        try {
            return SessionRegistryUtils.obtainSessionIdFromAuthentication(authentication);
        } catch (IllegalArgumentException ex) {//Do nothing
            Object session = getCurrentDetails();
            if(session instanceof String) return session.toString();
        }
        return null;
    }

    public static Object getCurrentDetails(){
        SecurityContext context = SecurityContextHolder.getContext();

        if(context == null) return null;
        Authentication authentication = context.getAuthentication();

        if(authentication == null) return null;

        return authentication.getDetails();
    }
}
