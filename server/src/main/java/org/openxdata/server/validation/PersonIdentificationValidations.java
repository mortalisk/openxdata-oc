package org.openxdata.server.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates any fields or attributes that can identify a person like cell number,email, TODO names etc.
 * 
 * @author maimoona kausar
 *
 */

public class PersonIdentificationValidations {

	public static boolean validateCellNumber(String cellNumber) {
		return DataValidation.validate(RegularExpressions.CELL_NUMBER, cellNumber, 7, 20);
	}
	public static boolean validateUsername(String username) {
		return DataValidation.validate(RegularExpressions.WORD_LOCALE, username,1,20);
	}
	
	/** 
	 * validates all email addresses even those of unicode characters. the validator 
	 * matches the address for being correct for international RFC standards.
	 * 
	 * for example the valid emails are
	 * Rδοκιμή@παράδειγμα.δοκιμή
	 * abc+dshsh@dhsjdh.ddd
	 * 
	 * can validate some invalid addresses but works 90% perfect
	 * 
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email) {
    	Matcher matcher;
    	Pattern pattern ;
    	pattern=Pattern.compile("\\.{2}|\\-{2}|_{2}");
    	matcher=pattern.matcher(email);
    	if(matcher.find()){
    		return false;
    	}
    	pattern=Pattern.compile("^\\@|^\\.|^\\-|^www\\.");
    	matcher=pattern.matcher(email);
    	if(matcher.find()){
    		return false;
    	}

    	return DataValidation.validate(RegularExpressions.EMAIL_STANDARD, email);
	}
}
