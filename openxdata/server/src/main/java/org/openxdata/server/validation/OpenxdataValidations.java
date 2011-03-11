package org.openxdata.server.validation;

import org.apache.commons.lang.StringUtils;

/**
 * validates parameters and values sent with in the project. i.e. not input by user. 
 * for example: serializer,action,studyid etc..
 * 
 * @author maimoona kausar
 *
 */
public class OpenxdataValidations {

    public static boolean validateActionParam(String param) {
        if (DataValidation.validate(RegularExpressions.WORD, param, 1, 20)) {
            return true;
        }
        return false;
    }

    public static boolean validateSerializerParam(String param) {
        if (DataValidation.validate(RegularExpressions.DOTTED_WORD, param, 1, 100)) {
            return true;
        }
        return false;
    }

    public static boolean validateIntegerParam(String param) {
        param = StringUtils.trim(param);
        try {
            Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
