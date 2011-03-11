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
package org.openxdata.server.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openxdata.test.XFormsFixture;

public class DefaultXformSerializerTest {
    
    private DefaultXformSerializer serializer = new DefaultXformSerializer();
    private ByteArrayOutputStream out;
    
    @Before
    public void setupUp() throws Exception {
        out = new ByteArrayOutputStream();
    }
    
    @After
    public void tearDown() throws Exception {
        if (out != null) {
            out.close();
        }
    }

    @Test
    public void testSerializeForms() throws Exception {
        // initialise method parameters
        List<String> data = new ArrayList<String>();
        data.add(XFormsFixture.getExampleXform1());
        data.add(XFormsFixture.getExampleXform2());
        // test call
        serializer.serializeForms(out, data, 1, "study", "");
        // assert output
        String actualOutput = out.toString();
        String size = String.valueOf(data.size());
        String actualSize = String.valueOf(actualOutput.getBytes()[0]);
        Assert.assertEquals("output stream starts with number of forms", actualSize, size);
        Assert.assertTrue("output stream contains xform1", actualOutput.contains(data.get(0)));
        Assert.assertTrue("output stream contains xform2", actualOutput.contains(data.get(1)));
    }
    
    @Test
    public void testSerializeStudy() throws Exception {
        // initialise test data
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(XFormsFixture.getStudy(1, "study one"));
        // test call
        serializer.serializeStudies(out, data);
        // assert output
        String actualOutput = out.toString();
        String id = String.valueOf(new Integer(1));
        String actualId = String.valueOf(actualOutput.getBytes()[0]);
        Assert.assertEquals("output stream starts with study id", actualId, id);
        Assert.assertTrue("output stream contains study info", actualOutput.endsWith("study one"));
    }
    
    @Test
    public void testSerializeUsers() throws Exception {
        // initialise test data
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(XFormsFixture.getUser(1, "dagmar", "timler", "blahblah#$blahblah"));
        // test call
        serializer.serializeUsers(out, data);
        // assert output
        String actualOutput = out.toString();
        String id = String.valueOf(new Integer(1));
        String actualId = String.valueOf(actualOutput.getBytes()[0]);
        Assert.assertEquals("output stream starts with user id", actualId, id);
        Assert.assertTrue("output stream contains user info", actualOutput.contains("dagmar"));
        Assert.assertTrue("output stream contains user info", actualOutput.contains("timler"));
        Assert.assertTrue("output stream contains user info", actualOutput.contains("blahblah#$blahblah"));
    }
    
	@Test
    @Ignore("The whole build system stops at this test 10% of the time.")
    public void testDeserialize() throws Exception {
    	final PipedOutputStream pout = new PipedOutputStream();
    	new Thread(
		    new Runnable(){
		      @Override
			public void run(){
		      	DataOutput output = new DataOutputStream(pout);
		        try {
		        	output.writeByte(2);
					output.writeUTF(XFormsFixture.getExampleXform1());
					output.writeUTF(XFormsFixture.getExampleXform2());
				} catch (IOException e) {
					e.printStackTrace();
				}
		      }
		    }
		  ).start();
    	DataInputStream in = new DataInputStream(new PipedInputStream(pout));
    	
        // test call
        List<String> forms = (List<String>)serializer.deSerialize(in, null);
        in.close();
        
        // assert output
        Assert.assertEquals("two forms deserialized", forms.size(), 2);
        Assert.assertEquals("first form correct", forms.get(0), XFormsFixture.getExampleXform1());
        Assert.assertEquals("second form correct", forms.get(1), XFormsFixture.getExampleXform2());
    }
}