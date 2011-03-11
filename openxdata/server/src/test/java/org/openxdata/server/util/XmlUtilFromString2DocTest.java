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
package org.openxdata.server.util;

import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.w3c.dom.Document;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.*;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class XmlUtilFromString2DocTest {

    private String plainXml =
            "<node1>"
            + "<node2>test</node2>"
            + "</node1>";
    private String withNamespaceAndPrefix =
            "<node4 xmlns:xxx=\"openxdata\">"
            + "<xxx:node5>test</xxx:node5>"
            + "</node4>";

    @Test
    public void testWithoutNamespace() {
        Document doc = XmlUtil.fromString2Doc(plainXml);
        Element node1 = doc.getDocumentElement();
        assertEquals("node1", node1.getTagName());
        assertEquals(1, node1.getChildNodes().getLength());

        NodeList node2s = doc.getElementsByTagName("node2");
        assertEquals(1, node2s.getLength());
        Element node2 = (Element) node2s.item(0);
        assertEquals("node2", node2.getTagName());
        assertEquals(1, node2.getChildNodes().getLength());
    }

    @Test
    public void testWithNamespace() {
        Document doc = XmlUtil.fromString2Doc(withNamespaceAndPrefix);
        Element node1 = doc.getDocumentElement();
        assertEquals("node4", node1.getTagName());
        assertEquals(1, node1.getChildNodes().getLength());

        NodeList node5s = doc.getElementsByTagNameNS("openxdata", "node5");
        assertEquals(1, node5s.getLength());
        Element node5 = (Element) node5s.item(0);
        assertEquals("xxx:node5", node5.getTagName());
        assertEquals("xxx", node5.getPrefix());
        assertEquals("node5", node5.getLocalName());
        assertEquals("openxdata", node5.getNamespaceURI());
        assertEquals(1, node5.getChildNodes().getLength());
    }

    @Test(expected = UnexpectedException.class)
    public void whenIOExceptionThenThrowUnexpectedException() throws IOException {
        PowerMock.mockStatic(IOUtils.class);
        final String xml = EasyMock.eq(plainXml);
        final String charset = EasyMock.eq("UTF-8");

        EasyMock.expect(IOUtils.toInputStream(xml, charset));
        EasyMock.expectLastCall().andThrow(new IOException());

        PowerMock.replayAll();
        XmlUtil.fromString2Doc(plainXml);
        PowerMock.verifyAll();
    }
}
