package org.openxdata.server.xform

import org.junit.Before
import org.junit.Test


class StudyImporterTest extends GroovyTestCase {

	def xml
	def study

	@Before void setUp(){

		def importer = new StudyImporter()
		def xmlString = '''<study name='test' description='Test Study' studyKey='Test Key'>
							<form name='Test Form' description='Test Description'>
							 <version name='Test Version'></version><version name='Test Version 1'></version>
							</form>
							<form name='Test Form 1' description='Test Description 1'>
							 <version name='Test Version 2'></version><version name='Test Version 3'></version>
							</form>
						  </study>'''

		xml = new XmlSlurper().parseText(xmlString)
		study = importer.importStudyFrom(xml)
	}

	@Test void testImportStudyDoesNotReturnNull(){

		assertNotNull study
	}

	@Test void testImportStudyReturnsValidStudyWithName(){

		assertEquals 'test', study.getName()
	}

	@Test void testImportStudyReturnsValidStudyWithDescription(){

		assertEquals 'Test Study', study.getDescription()
	}

	@Test void testImportStudyReturnsValidStudyWithStudyKey(){

		assertEquals 'Test Key', study.getStudyKey()
	}

	@Test void testImportStudyReturnsValidStudyWithFormElement(){

		def forms = study.getForms()

		assertNotNull forms
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectNumberOfForms(){

		def forms = study.getForms()

		assertEquals 2, forms.size()
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectFormName(){

		def form = study.getForm('Test Form')

		assertEquals 'Test Form', form.getName()

		def form1 = study.getForm('Test Form 1')

		assertEquals 'Test Form 1', form1.getName()
	}

	@Test void testImportStudyReturnsValidStudyWithCorrectFormDescription(){

		def form = study.getForm('Test Form')

		assertEquals 'Test Description', form.getDescription()
		
		def form1 = study.getForm('Test Form 1')
		
		assertEquals 'Test Description 1', form1.getDescription()
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormVersion(){
		def forms = study.getForms()
		forms.each{
			assertNotNull it.getVersions()
		}
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectNumberOfFormVersionsForEachForm(){
		def forms = study.getForms()
		forms.each{
			assertEquals 2, it.getVersions().size()
		}
	}
	
	@Test void testImportStudyReturnsValidStudyWithCorrectFormVersion() {
		
		def forms = study.getForms()
		
		def form1 = forms[0]
		
		assertEquals 'Test Version', form1.getVersion('Test Version').getName()
		assertEquals 'Test Version 1', form1.getVersion('Test Version 1').getName()
		
		def form2 = forms[1]
		assertEquals 'Test Version 2', form2.getVersion('Test Version 2').getName()
		assertEquals 'Test Version 3', form2.getVersion('Test Version 3').getName()
		
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormsHavingDefaultFormVersion() {
		
		def forms = study.getForms()
		
		def form1 = forms[0]
		
		assertNotNull form1.getDefaultVersion()
		assertEquals  'Test Version', form1.getDefaultVersion().getName()
		
		def form2 = forms[1]
		
		assertNotNull form2.getDefaultVersion()
		assertEquals  'Test Version 2', form2.getDefaultVersion().getName()
		
	}
	
	@Test void testImportStudyReturnsValidStudyWithFormVersionHavingXformElement(){
		
	}
}
