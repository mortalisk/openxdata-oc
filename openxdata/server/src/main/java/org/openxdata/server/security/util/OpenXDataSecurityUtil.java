/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
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
		User user = userDetails.getOXDUser();
		
		// Proceed to put the User in the spring security Context.
		SecurityContext sc = new SecurityContextImpl();
		Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), userDetails.getAuthorities());
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
}
