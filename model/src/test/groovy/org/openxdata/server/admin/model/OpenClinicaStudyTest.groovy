package org.openxdata.server.admin.model

import org.junit.Test

class OpenClinicaStudyTest extends GroovyTestCase {
	
	@Test void testOverriddenEqualsOnSameStudies(){
		
		def openclinicaStudy = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifier')
		def openclinicaStudy2 = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifier')
		
		assertEquals openclinicaStudy, openclinicaStudy2
	}
	
	@Test void testOverriddenEqualsOnDifferentStudies(){
		def openclinicaStudy = new OpenclinicaStudy(OID:'oidX',name:'name',identifier:'identifier')
		def openclinicaStudy2 = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifier')
		
		assertFalse openclinicaStudy.equals(openclinicaStudy2)
		
		def openclinicaStudyName = new OpenclinicaStudy(OID:'oid',name:'nameX',identifier:'identifier')
		def openclinicaStudyName2 = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifier')
		
		assertFalse openclinicaStudyName.equals(openclinicaStudyName2)
		
		def openclinicaStudyIdentifier = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifierX')
		def openclinicaStudyIdnetifier2 = new OpenclinicaStudy(OID:'oid',name:'name',identifier:'identifier')
		
		assertFalse openclinicaStudyIdentifier.equals(openclinicaStudyIdnetifier2)
	}
}
