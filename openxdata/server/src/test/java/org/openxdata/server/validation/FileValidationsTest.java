package org.openxdata.server.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * Tests File Validations
 * 
 * @author maimoona kausar
 *
 */
public class FileValidationsTest {
	@Test
	public void testValidateFileName() throws Exception {

		String[] fileNamesValid = new String[]{"glassfish-3.0.1","CmapToolsLogs","My_Cmaps","netbeans-6.9","2083436590_f899ee8925.jpg","art-isl abstract 00040","Generic SMS Application.gif","Nokia PC Suite 7.1.rar"
				,"Permissions-2.xlsx","islandart-350w-255x230.jpg","tango-icon-theme-0.8.90","ReminderResponseRecord_2010-11-03.zip","rxtx-2.1-7-bins-r2.zip","testcaseXout.xls_0.ods"
				,"java j2ee interviewgwt-ext and spring mvc_files","Java ServerPages JSP- java.itags.org_files"
				,"ms5366.aspx_files","Form Validation_ GWT_ Java_files","Surprisingly_Rockin_JavaScript_DOM_Prog_GWT.pdf","Surprisingly_Rockin_JavaScript_DOM_Prog_GWT","thread.jspa.html","windows.php.html"
				,"專營 以 較 便宜 的 價","錢 售 賣 日 本 品 牌 ","風格 如Rδοκι","μήπαράδειγ","μα.δοκιμή參","加大抽獎0拉視","乎參加人數抽參加"
				,"人數.拉視乎參","ç²æ–éå.æ—æœ","RÎÎÎºÎ¹Î¼ÎÏÎÏ","ÎÎÎµÎ¹Î³Î¼Î.Î","ÎÎºÎ¹Î¼PelÃ","sendåŽsampl","eviåçŸ ˆä "
				,"è¼ƒ ä¾åœ çš","åƒ¹éŒ å è³","æ— æœ å çŒ","çŽ 0æè– ä¹Žåƒ","åŠäººææŠ½åˆçŸ"
				,"ä è¼ƒ ä¾åœ","çšåƒ¹éŒ åè³æ— æœ","å çŒ éæ ¼ ååƒ","åŠ  å æŠ½ çŽ","0æè– ä¹Žåƒ","åŠäººæ æŠ½","åƒåŠ åæŠ½çŽ0"
				,"æè–ä¹ŽåƒåŠ","äººææŠ½"};
		String[] fileNamesInvalid = new String[]{"2576_1115456049327_1314721303_30337429_6143570_n.jpg","Log4j Tutorial| How to send the log messages to a File | Veera Sundar_files"
				,"ColorPicker.com : Quick Online Color Picker Tool_files","/home/maimoonak/Music/articleregx_files","JSP - JSP tutorial, example, code_files","fg \"dfkld\"cxvC","maim `k 12File","jamal ! Files","maim@kFile"
				,"pol:kjFile","fk ^grad","maira<and>jam","iftik;kaus","j2e'jhj'jsp","jam\\mam","jam/maam0","sort:insert"
				,"<miss m k >","whyFile?exit","now, never","Java ServerPages (JSP)- java.itags.org_files","ms536651#@!~$%^&*_-+=|:;<>,.?/VS.85).aspx_files"
				,"my`file","my~332file","my!!4ttfile","myf@34file","my#file","my$file","my%`file","my^vbfile","my&file"
				,"my*gjfile","mispalcedfile+found","my=file","my{own}file","my[own]file","my`file?noitsnot","sdh”ampleviå"
				,"sendåŽ�cx","sendåŽ»sampleviå","dsdh’sdjs","djxdj·sdjsd","sdsdjs¥zsh","sdsjd¬sdj","dsd¿aus","sdsd®sdd"
				,"sdhdj€ss","sxds±dsd","sasdhs©hssns","gdshgd°shs","dshdj<fgfdg>ddff","dhjh‡sjdhjs","sahsh¢hsajhs","bkjhkjh‰sgasg","sghg¤dsdj","sdd§sdn"};
		for (String string : fileNamesValid) {
			assertTrue("Valid File Name : "+string+",was rejected.",FileValidations.validateOutputFilename(string));
		}
		for (String string : fileNamesInvalid) {
			assertFalse("Invalid FileName: "+string+",was accepted.",FileValidations.validateOutputFilename(string));
		}
		
		String[] fileNamesValidnsp = new String[]{"glassfish-3.0.1","CmapToolsLogs","My_Cmaps","netbeans-6.9","2083436590_f899ee8925.jpg","art-islabstract00040","GenericSMSApplication.gif","NokiaPCSuite7.1.rar"
				,"Permissions-2.xlsx","islandart-350w-255x230.jpg","tango-icon-theme-0.8.90","ReminderResponseRecord_2010-11-03.zip","rxtx-2.1-7-bins-r2.zip","testcaseXout.xls_0.ods"
				,"java-j2ee-interviewgwt-ext-and-spring-mvc_files","Java-ServerPages-JSP--java.itags.org_files"
				,"ms5366.aspx_files","FormValidation_GWT_Java_files","Surprisingly_Rockin_JavaScript_DOM_Prog_GWT.pdf","Surprisingly_Rockin_JavaScript_DOM_Prog_GWT","thread.jspa.html","windows.php.html"
				,"專營以較便宜的價","錢售賣日本品牌","風格如Rδοκι","μήπαράδειγ","μα.δοκιμή參","加大抽獎0拉視","乎參加人數抽參加"
				,"人數.拉視乎參","ç²æ–éå.æ—æœ","RÎÎÎºÎ¹Î¼ÎÏÎÏ","ÎÎÎµÎ¹Î³Î¼Î.Î","ÎÎºÎ¹Î¼PelÃ","sendåŽsampl","eviåçŸˆä"
				,"è¼ƒä¾åœçš","åƒ¹éŒåè³","æ—æœåçŒ","çŽ0æèä¹Žåƒ","åŠäººææŠ½åˆçŸ"
				,"äè¼ƒä¾åœ","çšåƒ¹éŒåè³æ—æœ","åçŒéæ¼ååƒ","åŠåæŠ½çŽ","0æè–ä¹Žåƒ","åŠäººææŠ½","åƒåŠåæŠ½çŽ0"
				,"æè–ä¹ŽåƒåŠ","äººææŠ½"};
		String[] fileNamesInvalidnsp = new String[]{"2576_1115456049327_1314721303_30337429_6143570_n.jpg","Log4j Tutorial| How to send the log messages to a File | Veera Sundar_files"
				,"ColorPicker.com:QuickOnlinColorickerTool_files","/home/maimoonak/Music/articleregx_files","JSP-JSPtutorial,example,code_files","fg \"dfkld\"cxvC","maim `k 12File","jamal ! Files","maim@kFile"
				,"pol:kjFile","fk ^grad","maira<and>jam","iftik;kaus","j2e'jhj'jsp","jam\\mam","jam/maam0","sort:insert"
				,"<miss m k >","whyFile?exit","now,never","Java ServerPages (JSP)- java.itags.org_files","ms536651#@!~$%^&*_-+=|:;<>,.?/VS.85).aspx_files"
				,"my`file","my~332file","my!!4ttfile","myf@34file","my#file","my$file","my%`file","my^vbfile","my&file"
				,"my*gjfile","mispalcedfile+found","my=file","my{own}file","my[own]file","my`file?noitsnot","sdh”ampleviå"
				,"sendåŽ�cx","sendåŽ»sampleviå","dsdh’sdjs","djxdj·sdjsd","sdsdjs¥zsh","sdsjd¬sdj","dsd¿aus","sdsd®sdd"
				,"sdhdj€ss","sxds±dsd","sasdhs©hssns","gdshgd°shs","dshdj<fgfdg>ddff","dhjh‡sjdhjs","sahsh¢hsajhs","bkjhkjh‰sgasg","sghg¤dsdj","sdd§sdn"};

		for (String string : fileNamesValidnsp) {
			assertTrue("Valid NoSpaceFile Name : "+string+",was rejected.",FileValidations.validateNoSpaceFilename(string));
		}
		for (String string : fileNamesInvalidnsp) {
			assertFalse("Invalid NoSpaceFileFileName: "+string+",was accepted.",FileValidations.validateNoSpaceFilename(string));
		}
	}
}
