package org.openxdata.server.xform

import org.junit.Before
import org.junit.Test


class StudyImporterTest extends GroovyTestCase {

	def xml
	def study
	def forms
	def importer

	@Before void setUp(){

		def xmlString = '''<study name='test' description='Test Study' studyKey='Test Key'>
							<form name='Test Form' description='Test Description'>
							 <version name='Test Version'></version><version name='Test Version 1'></version>
							</form>
							<form name='Test Form 1' description='Test Description 1'>
							 <version name='Test Version 2'></version><version name='Test Version 3'></version>
							</form>
						  </study>'''

		xml = new XmlSlurper().parseText(xmlString)

		importer = new StudyImporter(xml)
		study = importer.importStudyFrom()
		
		forms = study.getForms()
	}

	@Test void testImportStudyDoesNotReturnNull(){

		assertNotNull study
	}
	
	@Test void testImportStudyReturnsANewStudy(){
		assertTrue study.isNew()
	}

	@Test void testImportStudyReturnsValidStudyWithName(){

		assertEquals 'test', study.toString()
	}

	@Test void testImportStudyReturnsValidStudyWithNameOnToString(){

		assertEquals 'test', study.toString()
	}

	@Test void testImportStudyReturnsValidStudyWithDescription(){

		assertEquals 'Test Study', study.getDescription()
	}

	@Test void testImportStudyReturnsValidStudyWithStudyKey(){

		assertEquals 'Test Key', study.getStudyKey()
	}

	@Test void testImportStudyReturnsValidStudyWithFormElement(){

		assertNotNull forms
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectNumberOfForms(){

		assertEquals 2, forms.size()
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectFormName(){

		def form = study.getForm('Test Form')

		assertEquals 'Test Form', form.getName()

	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectFormName2(){

		def form1 = study.getForm('Test Form 1')

		assertEquals 'Test Form 1', form1.getName()
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectFormDescription(){

		def form = study.getForm('Test Form')

		assertEquals 'Test Description', form.getDescription()
		
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectFormDescription2(){

		def form1 = study.getForm('Test Form 1')

		assertEquals 'Test Description 1', form1.getDescription()
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormVersion() {
		
		forms.each{
			assertNotNull it.getVersions()
		}
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectNumberOfFormVersionsForEachForm() {
		
		forms.each{
			assertEquals 2, it.getVersions().size()
		}
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectFormVersion() {
				
		def form1 = forms[0]
		
		assertEquals 'Test Version', form1.getVersion('Test Version').getName()
		assertEquals 'Test Version 1', form1.getVersion('Test Version 1').getName()
		
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectFormVersion2() {

		def form2 = forms[1]
		assertEquals 'Test Version 2', form2.getVersion('Test Version 2').getName()
		assertEquals 'Test Version 3', form2.getVersion('Test Version 3').getName()
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormsHavingDefaultFormVersion() {
				
		def form1 = forms[0]
		
		assertNotNull form1.getDefaultVersion()
		assertEquals  'Test Version', form1.getDefaultVersion().getName()
		
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormsHavingDefaultFormVersion2() {

		def form2 = forms[1]

		assertNotNull form2.getDefaultVersion()
		assertEquals  'Test Version 2', form2.getDefaultVersion().getName()
	}
	
	@Test void testSetXformReturnsValidStudyWithFormVersionHavingXformElement() {
		
		def studyWithXform = importer.importXform()
		def form = studyWithXform.getForm('Test Form')
		
		def version = form.getVersion('Test Version')
		
		assertNotNull version.getXform()
	}
}
