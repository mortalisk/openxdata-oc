package org.openxdata.server.validation;
import org.junit.Ignore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests PersonIdentificationValidations like cell number format
 * 
 * @author maimoona kausar
 *
 */
public class PersonIdentificationValidationsTest {
	@Test
	public void testValidateCellNumbers() throws Exception {

		String[] valid = new String[]{"+923343000001","+10029839233333","922223982832",
				"042343454","0839233","+0839233","+92-334-3000000","+92 334 3000000","92-334-3000000",
				"+92 334-3000-000","92 334-3000-000","92-334 300 0000"};
		String[] invalid = new String[]{"++92-334-3000000","+92+334-3000000","+92-334+3000000",
				"+92-334-3000000+","+923343000000+","+-92-334-3000000","92--334-3000000","+92-334--3000000",
				"+92- 334-3000000","92 -334-3000000","+92-334-3000000-","+ 92-334-3000000","+92-334-30000a0",
				"+92-334-3ad0000","+92334_3000000","92-334-30_00000","+92,334,3000000","+92-334-300.0000",
				"-92-334-3000000","092334300000087897898","+92-334-3000000.","92-3|34-3000000","+92#334-3000000","+92@334-3000000",
				"+92-334-30&00000","<+92-334-3000000>"};

		for (String string : valid) {
			assertTrue("Valid CellNumber : "+string+",was rejected.",PersonIdentificationValidations.validateCellNumber(string));
		}
		for (String string : invalid) {
			assertFalse("Invalid CellNumber: "+string+",was accepted.",PersonIdentificationValidations.validateCellNumber(string));
		}

	}
	@Test
    @Ignore(value="Please fix failing tests")
	public void testValidateEmails() throws Exception {
		String[] valids=new String[]{"aa.nn.nnkk@sdksj.sdks","sdhsd3473@dkjskd.dfkd","dsdgdd_ncc.dsjdk@kdsd.dfdf"
				,"fsdhf~4548758gbdfg@fdfdf.dfdf","\"\"test\\\\blah\"\"@example.com","\"test/blah\"@example.com","\"\"no not at all\"\"@example.com"
				,"aa-dsd.sds_dsv@djwdk-dd.dd_wh.dsd","dfsf.dfsfd+dsd@sdhjsd.sds","\"test\\\\rblah\"@example.com"
				,"\"~!#$%@^%&*&_+|FGSFDSF43dscdxvftre\"@dsjkdj.ffd","\"test\\\\rblah\"@example.com","\"\"test\\\"\"blah\"\"@example.com"
				,"customer/department@example.com","!def!xyz%abc@example.com","_Yosemite.Sam@example.com","~@example.com",
				"\"\"Austin@Powers\"\"@example.com","\"\"Im.mk\"\"@example.com","\"\"Im mk\"\"@example.com"
				,"dsh.djd-dfjdf_sdhsjd+dshd@hfdjfh.dsfh","ç”²æ–�@é»’å·�.æ—¥æœ¬","RÎ´Î¿ÎºÎ¹Î¼Î®@Ï€Î±Ï�Î¬Î´ÎµÎ¹Î³Î¼Î±.Î´Î¿ÎºÎ¹Î¼Î®","PelÃ©@example.com"
				,"\"å°ˆç‡Ÿ ä»¥ è¼ƒ ä¾¿å®œ çš„ åƒ¹éŒ¢ å”® è³£ æ—¥ æœ¬ å“� ç‰Œ é¢¨æ ¼ å¦‚.fdhfdf\"@hsjkfdh.cgh","sendåŽ»samplevis@gmail.com"
				,"\"å�ƒ åŠ  å¤§ æŠ½ ç�Ž 0æ‹‰! è¦– ä¹Žå�ƒ åŠ  äººæ•¸, æŠ½\"@dfhjd.xcx","å�ƒåŠ å¤§æŠ½ç�Ž0æ‹‰!è¦–ä¹Žå�ƒåŠ äººæ•¸æŠ½@dhfjd.dfd"
				,"sdjshfj@sjdsd-sjdhs-sdh_sdg.sdgs.sdh","chjh.sds_23278@shd.sdgs-sgd.sdd","dhsdhs@fdfdf.fdf"
				,"agshags@dfnd-fjdkfj.dfjs","dffhdhf@dfdjfh268763.sfdhs.dwg","user+mailbox+mine@gmail.org"};
		
				
		for (String string : valids) {
			assertTrue("Valid Email : "+string+",was rejected.",PersonIdentificationValidations.validateEmail(string));
		}
		
		String[] invalids=new String[]{"@jskad.sdsld@wdksld.fkd","sdsd@dsjdk@fdslf.fdkf",".fjd.fjsdf@dsd.dsd"
				,"-jdch.fdhcd@fdjf.cdcd","dhsjdh-dsjd.@sjds.fjd","sdsj.sdkjs_djs-sd@-sdjhs.fsdf","fdf.sd@dsk.df."
				,"dfdf@dfdf.dfd-fdfd","dfdf(ghg)dfgd@ksd.dfd","fdf..fdf@fdf.sdd","sdfsf--fsd@kds.sfd","dfdf@sfdsf"
				,"dfdfds.sdfdff","sfdsfd-sfdf","dfdfddff","fdgfdfd__dfdf@jfsk.sdjsd","www.xcxc@cvjkc.xcd"
				,"","NotAnEmail","@NotAnEmail","\"test\rblah\"@example.com"/*,"\"\"test\blah\"\"@example.com"*/
				,".wooly@example.com","wo..oly@example.com","pootietang.@example.com",".@example.com","Im mk@example.com"
				,"å°ˆç‡Ÿ ä»¥ è¼ƒ ä¾¿å®œ çš„ åƒ¹éŒ¢ å”® è³£ æ—¥ æœ¬ å“� ç‰Œ é¢¨æ ¼ å¦‚.fdhfdf@hsjkfdh.cgh","å�ƒ åŠ  å¤§ æŠ½ ç�Ž 0æ‹‰! è¦– ä¹Žå�ƒ åŠ  äººæ•¸, æŠ½ @dfhd.ssd"
				,"fjdhf@[38473.sdhsd-sdjskd.fdsf.sas]"};
		for (String string : invalids) {
			assertFalse("Invalid Email: "+string+",was accepted.",PersonIdentificationValidations.validateEmail(string));
		}
	}

}
