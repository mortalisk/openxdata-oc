package org.openxdata.server.validation;

/**
 * Validates filenames
 * TODO validate any field or attributes related to files
 * 
 * @author maimoona kausar
 *
 */
public class FileValidations {

    public static boolean validateOutputFilename(String filename) {
        return DataValidation.validate(RegularExpressions.FILE_NAME_RESTRICTED, filename, 1, 50);
    }

    public static boolean validateNoSpaceFilename(String filename) {
        return DataValidation.validate(RegularExpressions.FILE_NAME_RESTRICTED_NOSPACE, filename, 1, 50);
    }
}
