package org.openxdata.server.service;

import groovy.mock.interceptor.MockFor

import org.gmock.WithGMock
import org.junit.Before
import org.junit.Test
import org.openxdata.oc.transport.OpenClinicaSoapClient
import org.openxdata.server.service.impl.OpenclinicaServiceImpl

@WithGMock
class OpenClinicaServiceTest extends GroovyTestCase {


	def openclinicaService

	@Before public void setUp(){
		def xformXml = getClass().getClassLoader().getResourceAsStream('org/openxdata/server/service/impl/convertedOpenXdataSampleForm.xml').text

		def mock = new MockFor(OpenClinicaSoapClient)
		mock.demand.getOpenxdataForm { return xformXml }

		def client = mock.proxyDelegateInstance()
		openclinicaService = new OpenclinicaServiceImpl(client:client)
	}

	@Test public void testImportOpenClinicaStudyShouldNotReturnNull() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		assertNotNull xform
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithStudyElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		assertEquals 'study', xml.name()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithStudyName() {
		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		assertEquals 'Default Study', xml.@name.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithStudyKey() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		assertEquals 'S_DEFAULTS1', xml.@studyKey.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithStudyDescription() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		assertEquals 'test instance', xml.@description.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithFormElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def form = xml.form
		assertEquals 'form', form.name()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithFormName() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def form = xml.form
		assertEquals 'SE_SC1', form.@name.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithFormDescription() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def form = xml.form
		assertEquals 'SC1', form.@description.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithFormVersionElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def formVersion = xml.version
		assertEquals 'version', formVersion.name()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithFormVersionName() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def formVersion = xml.form.version
		assertEquals 'SC1-v1', formVersion.@name.text()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithXformElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def xformElement = xml.form.version.xform
		assertEquals 'xform', xformElement.name()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithXformsElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def xformsElement = xml.form.version.xform.xforms
		assertEquals 'xforms', xformsElement.name()
	}

	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithXformsElementWithNamespace() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def xformsElement = xml.form.version.xform.xforms
		def namespaceList = xformsElement.'**'.collect { it.namespaceURI() }.unique()
		
		def actual = 'http://www.w3.org/2002/xforms'
		assertEquals actual, namespaceList[0].toString()
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithModelElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def model = xml.form.version.xform.xforms.model
		assertEquals 'model', model.name()
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithModelContainingInstanceElement() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def instance = xml.form.version.xform.xforms.model.instance
		assertEquals 'instance', instance.name()
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithModelContainingInstanceElementHavingID() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def instance = xml.form.version.xform.xforms.model.instance
		
		assertEquals 'S_DEFAULTS1 - SE_SC1', instance.@id.text()
	}
	
	@Test public void testInstanceIDEqualsStudyNameAndFormNameWithHyphenInBetween() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def instance = xml.form.version.xform.xforms.model.instance

		def actual = instance.@id.text()
		def studyFormNameCombo = "${xml.@studyKey.text()} - ${xml.form.@name.text()}"
		
		assertEquals actual, studyFormNameCombo.toString()
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithModelContainingBindElements() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def binds = xml.form.version.xform.xforms.model.bind

		assertNotNull binds
	}
	
	@Test public void testImportOpenClinicaStudyShouldReturnValidXformWithModelContainingCorrectSizeBindElements() {

		def xform = openclinicaService.importOpenClinicaStudy("identifier")

		def xml = new XmlSlurper().parseText(xform)
		def binds = xml.form.version.xform.xforms.model.bind

		assertEquals 527, binds.size()
	}

}
