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

import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 *
 * @author Jonny Heggheim
 */
public class XmlUtilGetNodeValueTest {

    private String plainXml =
            "<node1>"
            + "<node2>test</node2>"
            + "</node1>";
    private String withAttribute =
            "<node1 aa=\"test2\">"
            + "<node2 bb=\"test3\">test</node2>"
            + "</node1>";

    @Test
    public void withPlainXml() {
        Document document = XmlUtil.fromString2Doc(plainXml);
        String expected = "test";
        String actual = XmlUtil.getNodeValue(document, "/node1/node2");
        assertEquals(expected, actual);
    }

    @Test
    public void checkOnAttributeOnRoot() {
        Document document = XmlUtil.fromString2Doc(withAttribute);
        String expected = "test2";
        String actual = XmlUtil.getNodeValue(document, "/node1/@aa");
        assertEquals(expected, actual);
    }

    @Test
    public void checkOnAttributeOnChild() {
        Document document = XmlUtil.fromString2Doc(withAttribute);
        String expected = "test3";
        String actual = XmlUtil.getNodeValue(document, "/node1/node2/@bb");
        assertEquals(expected, actual);
    }

    @Test
    public void whenNoValuesFoundThenItShouldReturnEmptyString() {
        Document document = XmlUtil.fromString2Doc(withAttribute);
        String expected = "";
        String actual = XmlUtil.getNodeValue(document, "/node1/xxx");
        assertEquals(expected, actual);
    }

    @Test
    public void whenNoAttributesFoundThenItShouldReturnEmptyString() {
        Document document = XmlUtil.fromString2Doc(withAttribute);
        String expected = "";
        String actual = XmlUtil.getNodeValue(document, "/node1/node2/@xxx");
        assertEquals(expected, actual);
    }
}
