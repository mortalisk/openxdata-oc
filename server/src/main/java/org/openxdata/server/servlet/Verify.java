package org.openxdata.server.servlet;

/**
 *
 * @author Jonny Heggheim
 */
class Verify {

    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }

        return string.trim().length() == 0;
    }

    public static boolean isValidId(String id) {
        if (isNullOrEmpty(id)) {
            return false;
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException idIsNotANumber) {
            return false;
        }

        return true;
    }

    public static boolean isValidDate(String date) {
        return isValidLong(date);
    }

    public static boolean isValidLong(String number) {
        if(isNullOrEmpty(number)) {
            return false;
        }
        try {
            Long.parseLong(number);
        } catch (NumberFormatException isNotANumber) {
            return false;
        }

        return true;
    }

    public static boolean isValidType(String type) {
        return !isNullOrEmpty(type);
    }
}
