package org.openxdata.server.validation;

/** 
 * Defines Regular expressions to match different criteria. like word, alpha , numbers,email,filename,etc
 * 
 * @author maimoona kausar
 * */
public class RegularExpressions {

    /** Only [a-zA-z0-9_]= a to z, A to Z, 0 to 9 and (_) underscore */
    public static final String WORD = "\\w+";
    /** Only [a-zA-z0-9_.]= a to z, A to Z, 0 to 9, (.) Dot and (_) underscore */
    public static final String DOTTED_WORD = "\\w+|(\\w+\\.\\w+)+";
    /** Only Letters numbers and connectors like (_) underscore etc*/
    public static final String WORD_LOCALE = "[\\p{L}\\p{M}\\p{N}\\p{Pc}]+";
    /** Only [a-zA-z]= a to z and A to Z */
    public static final String ALPHA = "[a-zA-Z]+";
    /** Only [0-9]= 0 to 9 */
    public static final String NUMERIC = "[0-9]+";
    /** Only [a-zA-z0-9]= a to z, A to Z and 0 to 9*/
    public static final String ALPHA_NUMERIC = "[a-zA-Z0-9]+";
    /** Only format ([+]0-9(with [,- space])) e.g. +92239239293232,+92,3232,2323,+92-382-23,928-3928-333
     * 										9302932392,92 323 323 32 etc */
    public static final String PHONE_NUMBER = "\\+?(\\d+([,\\-\\s]?)\\d+)+";
    /** Only format ([+]0-9(with [- space])) e.g. +92239239293232,+92 3232 2323+92-382-23,928-3928-333
     * 										9302932392,92 323 323 32 etc */
    public static final String CELL_NUMBER = "\\+?(\\d+([\\-\\s]?)\\d+)+";
    /** Only [a-zA-z0-9_space]= a to z, A to Z, 0 to 9, (_) underscore and ( ) space */
    public static final String WHITESPACE_WORD = "[\\w\\s]+";
    /** Only [a-zA-z space]= a to z, A to Z and ( ) space */
    public static final String WHITESPACE_ALPHA = "[a-zA-Z\\s]+";
    /** Only [0-9 space]= 0 to 9 and ( ) space */
    public static final String WHITESPACE_NUMERIC = "[0-9\\s]+";
    /** Only [a-zA-z0-9space]= a to z, A to Z, 0 to 9 and ( ) space */
    public static final String WHITESPACE_ALPHA_NUMERIC = "[a-zA-Z0-9\\s]+";
    public static final String FILE_NAME_RESTRICTED_NOSPACE = "[\\.\\p{L}\\p{Mn}\\p{Mc}\\p{N}\\p{Pc}\\p{Pd}]+";
    public static final String FILE_NAME_RESTRICTED = "[\\.\\p{L}\\p{Mn}\\p{Mc}\\p{N}\\p{Pc}\\p{Zs}\\p{Pd}]+";
    private static String local = "[\\p{L}\\p{M}\\p{S}\\p{N}\\p{Pd}\\p{Pc}/]+";// these valid characters should be added 
    // to completely match RFC standards 
    // ! # $ % & ' * + - / = ?  ^ _ ` . { | } ~\
    private static String domain = "([\\p{L}\\p{M}\\p{N}]+|([\\p{L}\\p{M}\\p{N}]+[\\.\\p{Pd}\\p{Pc}][\\p{L}\\p{M}\\p{N}]+))+\\.[\\p{L}\\p{M}\\p{N}]+";
    private static String localspq = "(\\p{Pi}|\").*(\\p{Pf}|\")";
    /** 
     * validates all email addresses even those of unicode characters. the validator 
     * matches the address for being correct for international RFC standards.
     * 
     * for example the valid emails are
     * Rδοκιμή@παράδειγμα.δοκιμή
     * abc+dshsh@dhsjdh.ddd
     *  */
    public static final String EMAIL_STANDARD = "(" + local + "|" + localspq + "|((" + local + "|" + localspq + ")[\\.\\p{Pd}\\p{Pc}](" + local + "|" + localspq + ")))+\\@" + "(" + domain + ")";

    private RegularExpressions() {
    }
}
