package org.openxdata.server.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * Test OpenxdataValidations like action, serializer, interger parameters
 * 
 * @author maimoona kausar
 *
 */
public class OpenxdataValidationsTest {

	@Test
	public void testValidateActionParam(){
		String[] valid=new String[]{"change","edit","do_work","change_oxd_frontend","edit_p2","OPEN"
				,"DOWNLOAD_FORMS","downloadstudies","downloadusers","download_users"
				,"downloaduser1","download_user_1","download3users","downloadusers321","download_372_users"};
		String[] invalid=new String[]{"download users","down load users","download.users","download-users"
				,"download/users","download+users","download(users)","download<users>","download|users"
				,"REQUEST_ACTION_DOWNLOAD_FORMS","download321_372_users"};

		for (String string : valid) {
			assertTrue("Valid Param : "+string+",was rejected.",OpenxdataValidations.validateActionParam(string));
		}
		for (String string : invalid) {
			assertFalse("Invalid Param : "+string+",was accepted.",OpenxdataValidations.validateActionParam(string));
		}
	}
	@Test
	public void testValidateSerializerParam(){
		String[] valid=new String[]{"serializer","serializer1","serializer321","serializer_123_users"
				,"serializer_serial22","serializer_serial_number_30","serializer.user","serializer.user.s"
				,"org.openxdata.server.serializer.DefaultXformSerializer","org.openxdata.server.serializer30.Default_Xform_Serializer"};
		String[] invalid=new String[]{"org.openxdata.",".serializer.DefaultXformSerializer","org.openxdata.server.serializer.Default Xform Serializer"
				,"org.openxdata..server.serializer.DefaultXformSerializer","org.openxdata.server.serializer.Default/XformSerializer"
				,"org.openxdata.server.serializer.DefaultXformSerializer.sdhsdhhdjshdsjh.sdshdjhsdj.sgdhsgd.sdsdj.sdskdjjdjdj"
				,"serial#1","org.openxdata.server.serializer.DefaultXformSerializer.."};

		for (String string : valid) {
			assertTrue("Valid Param : "+string+",was rejected.",OpenxdataValidations.validateSerializerParam(string));
		}
		for (String string : invalid) {
			assertFalse("Invalid Param : "+string+",was accepted.",OpenxdataValidations.validateSerializerParam(string));
		}
	}
	@Test
	public void testValidateIntegerParam(){
		String[] valid=new String[]{"  1213","2211  "," 233 ","23213213","23213213","23321"
				,"00001","0021200","21212","-213213"};
		String[] invalid=new String[]{"121 212","+2233","1212.212.12","","    ","  23712 343 "," 347 33","223 s"
				,"23231223123123123123233","ds2321131","12121e2323","2312312b","ededfff"};

		for (String string : valid) {
			assertTrue("Valid Param : "+string+",was rejected.",OpenxdataValidations.validateIntegerParam(string));
		}
		for (String string : invalid) {
			assertFalse("Invalid Param : "+string+",was accepted.",OpenxdataValidations.validateIntegerParam(string));
		}
	}
}
