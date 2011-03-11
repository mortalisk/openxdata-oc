/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.servlet;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.security.OpenXDataUserDetails;
import org.openxdata.server.security.OpenXdataUserDetailsService;
import org.openxdata.server.service.AuthenticationService;
import org.openxdata.server.service.UserService;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;

public class DataImportServletTest {
	
	DataImportServlet servlet;
	
	UserService userServiceMock;
	AuthenticationService authServiceMock;
	
	OpenXdataUserDetailsService userDetailsServiceMock;
	
	@Before
	public void setup() throws Exception {
		servlet = new DataImportServlet();
		
		userServiceMock = EasyMock.createMock(UserService.class);
		authServiceMock = EasyMock.createMock(AuthenticationService.class);
		userDetailsServiceMock = EasyMock.createMock(OpenXdataUserDetailsService.class);
		
		servlet.setUserService(userServiceMock);
		servlet.setAuthenticationService(authServiceMock);
		servlet.setUserDetailsService(userDetailsServiceMock);
	}

	@Test
	public void testGetUser() throws Exception {
		// test data
		String username = "guyzb";
		String password = "daniel123";
	    byte[] userPass = ( username + ":" + password ).getBytes();
	    String encodedUserPass = new String(Base64.encodeBase64(userPass));
	    User user = new User(username, password);

	    // set up mocks
	    EasyMock.expect(authServiceMock.authenticate(username, password)).andReturn(user);
	    EasyMock.replay(authServiceMock);
	    
	    // test
		servlet.getUser("BASIC "+encodedUserPass);
		
		// assert/verify
		EasyMock.verify(authServiceMock);
	}
	
	@Test
	public void testGetMSISDNUser() throws Exception {
		// test data
		String msisdn = "0768198075";
		String username = "dagmar";
		User user = new User(username);
		user.setPassword("password");
		GrantedAuthority[] authorities = { new GrantedAuthorityImpl("Perm_Test") };
		OpenXDataUserDetails userDetails = new OpenXDataUserDetails(user, true, true, true, true, authorities);
		
	    // set up mocks
	    EasyMock.expect(userServiceMock.findUserByPhoneNo(msisdn)).andReturn(user);
	    EasyMock.replay(userServiceMock);
	    EasyMock.expect(userDetailsServiceMock.loadUserByUsername(username)).andReturn(userDetails);
	    EasyMock.replay(userDetailsServiceMock);

	    // test
		servlet.authenticateUserBasedOnMsisd(msisdn);
		
		// assert/verify
		EasyMock.verify(userServiceMock);
		EasyMock.verify(userDetailsServiceMock);
	}
	
	@Test
	@Ignore("this is an integration test for localhost")
	public void integrationTest() throws Exception {
	    final String urlString = "http://localhost:8888/org.openxdata.server.admin.OpenXDataServerAdmin/dataimport?msisdn=2222222";
	    final String userName = "dagmar";
	    final String password = "dagmar";

	    // open url connection
	    URL url = new URL(urlString);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");
	    con.setDoOutput(true);
	    con.setDoInput(true);
	    con.setChunkedStreamingMode(1024);

	    // stuff the Authorization request header
	    byte[] encodedPassword = ( userName + ":" + password ).getBytes();
	    con.setRequestProperty( "Authorization", "Basic " + new String(Base64.encodeBase64(encodedPassword)));
	    
	    // put the form data on the output stream
	    DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeByte(new Integer(1));
		String data = "<?xml version='1.0' encoding='UTF-8' ?><cmt_lesotho_tlpp_session_report_v1 formKey=\"cmt_lesotho_tlpp_session_report_v1\" id=\"2\" name=\"Treatment literacy programme_v1\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">  <name>TEEHEE</name>  <session_type>treatment_literacy_session</session_type>  <session_date>2010-04-15</session_date>  <district>berea</district>  <session_location>secondary_school</session_location>  <session_location_other/>  <session_place_name>dsds</session_place_name>  <participant_number_male>1</participant_number_male>  <participant_number_female>2</participant_number_female>  <session_start_time>10:22:00</session_start_time>  <session_finish_time>01:22:00</session_finish_time>  <session_topic>children_HIV_incl_ARVs</session_topic>  <session_topic_other/>  <support_group>false</support_group>  <one_on_one_session>false</one_on_one_session>  <CMT_AV_kit>false</CMT_AV_kit>  <CMT_DVD>false</CMT_DVD>  <CMT_co_facilitator>false</CMT_co_facilitator>  <co_facilitator_name/>  <clinic_hospital_department/>  <clinic_hospital_department_other/>  <clinic_hospital_TV/>  <school_grade>12</school_grade>  <school_CMT_materials>true</school_CMT_materials>  <session_confirmed>true</session_confirmed></cmt_lesotho_tlpp_session_report_v1>";
		out.writeUTF(data);
		out.flush();

	    // pull the information back from the URL
	    InputStream is = con.getInputStream();
	    StringBuffer buf = new StringBuffer();
	    int c;
	    while ((c = is.read()) != -1) {
	      buf.append((char)c);
	    }
	    con.disconnect();

	    // output the information
	    System.out.println(buf);
	}
}
