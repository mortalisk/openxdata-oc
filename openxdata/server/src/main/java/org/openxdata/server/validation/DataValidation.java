package org.openxdata.server.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class provides functions to validate strings using different predefined regular expressions and length limits
 * 
 *  @author maimoona kausar
 */
class DataValidation {

    public static boolean validate(String expression, String string) {
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    public static boolean validate(String expression, String string, int minln, int maxln) {
        if (string == null || string.length() < minln || string.length() > maxln) {
            return false;
        }
        return validate(expression, string);
    }
}
