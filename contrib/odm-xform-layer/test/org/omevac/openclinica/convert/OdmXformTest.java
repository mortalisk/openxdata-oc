package org.omevac.openclinica.convert;

import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

/**
 * Unit test for the ODM to XForms conversion.
 * 
 * @author daniel
 *
 */
public class OdmXformTest extends TestCase{

	public void testFromOdm2Xform(){
		OdmXform converter = new OdmXform();
		String xform = converter.fromOdm2Xform(getTestOdm());
		assertNotNull(xform);
		
		//System.out.println("XForm: \n" + xform);
		try{
	        File outFile = new File("c:\\OdmXformTest.xml");
	        FileWriter out = new FileWriter(outFile);
	        out.write(xform);
	        out.close();
		}catch(Exception ex){ex.printStackTrace();}
	}
	
	private String getTestOdm(){
		return "<?xml version='1.0' encoding='UTF-8' ?> " +
		" <ODM xmlns='http://www.cdisc.org/ns/odm/v1.3' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.cdisc.org/ns/odm/v1.3 ODM1-3.xsd' ODMVersion='1.3' FileOID='1D20080412202420' FileType='Snapshot' Description='First dataset for testing of ODM' CreationDateTime='2008-04-12T20:24:20'> " +
			" <ClinicalData StudyOID='S_DEFAULTS1' MetaDataVersionOID='v1.0.0'> " +
				" <SubjectData SubjectKey='SS_STUDYSUB'> " +
					" <StudyEventData StudyEventOID='SE_INITIALS'> " +
						" <FormData FormOID='F_CANC_V10'> " +
							" <ItemGroupData ItemGroupOID='IG_CANC_UNGROUPED' TransactionType='Insert'> " +
								" <ItemData ItemOID='I_CANC_CAN_BASE1' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE2' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE3' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE4' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE5' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE6' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE7' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE8' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE9' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE10' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE11' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE12' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE13' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE14' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE15' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE16' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE17' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE18' Value='a' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE19' Value='2' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE20' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE21' Value='1' />  " +
								" <ItemData ItemOID='I_CANC_CAN_BASE22' Value='1' />  " +
							" </ItemGroupData> " +
						" </FormData> " +
					" </StudyEventData> " +
				" </SubjectData> " +
			" </ClinicalData> " +
		" </ODM> ";
	}
}
