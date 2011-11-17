package org.openxdata.server.service;

import groovy.mock.interceptor.MockFor

import org.gmock.WithGMock
import org.junit.Before
import org.junit.Test
import org.openxdata.oc.transport.OpenClinicaSoapClient
import org.openxdata.server.dao.StudyDAO
import org.openxdata.server.service.impl.OpenclinicaServiceImpl

@WithGMock
class OpenClinicaServiceTest extends GroovyTestCase {


	def study

	@Before public void setUp(){
		
		def xformXml = getClass().getClassLoader().getResourceAsStream('org/openxdata/server/service/impl/convertedOpenXdataSampleForm.xml').text
		
		def xml = new XmlSlurper().parseText(xformXml)
		
		def clientMock = new MockFor(OpenClinicaSoapClient)
		clientMock.demand.getOpenxdataForm { return xml }

		def client = clientMock.proxyDelegateInstance()
				
		def studyDAOMock = new MockFor(StudyDAO)
		studyDAOMock.demand.saveStudy { }

		def studyDAO = studyDAOMock.proxyDelegateInstance()
		
		def openclinicaService = new OpenclinicaServiceImpl(client:client, studyDAO:studyDAO)
		
		study = openclinicaService.importOpenClinicaStudy("identifier")
	}

	@Test public void testImportOpenClinicaStudyShouldNotReturnNull() {

		assertNotNull study
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithStudyName() {
		
		assertEquals 'Default Study', study.getName()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithStudyKey() {

		assertEquals 'S_DEFAULTS1', study.getStudyKey()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithStudyDescription() {

		assertEquals 'test instance', study.getDescription()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithForms() {

		def forms = study.getForms()
		assertNotNull forms
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithCorrectNumberOfForms() {

		def forms = study.getForms()
		assertEquals 1, forms.size()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidXformWithFormName() {

		def form = study.getForm('SE_SC1')
		assertEquals 'SE_SC1', form.getName()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidXformWithFormDescription() {

		def form = study.getForm('SE_SC1')
		assertEquals 'SC1', form.getDescription()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithFormVersion() {

		def form = study.getForm('SE_SC1')
		def versions = form.getVersions()
		assertEquals 1, versions.size()
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithFormVersionName() {

		def form = study.getForm('SE_SC1')
		def version = form.getVersion('SC1-v1')
		assertEquals 'SC1-v1', version.getName()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnsValidStudyWithDefaultFormVersion() {

		def form = study.getForm('SE_SC1')
		def version = form.getDefaultVersion()
		assertTrue version.getIsDefault()
	}
		
	@Test public void testInstanceIDEqualsStudyNameAndFormNameWithHyphenInBetween() {

		def form = study.getForm('SE_SC1')
		def version = form.getVersion('SC1-v1')
		assertTrue version.getName().contains('-')
		
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXStudyWithFormVersionXform() {


		def form = study.getForm('SE_SC1')
		def version = form.getDefaultVersion()

		def xform = version.getXform()
		
		assertNotNull xform
	}
}
