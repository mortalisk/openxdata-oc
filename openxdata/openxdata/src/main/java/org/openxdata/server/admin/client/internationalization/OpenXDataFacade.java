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
package org.openxdata.server.admin.client.internationalization;

import com.google.gwt.i18n.client.Dictionary;

/**
 * Abstracts the retrieval of constants to be used in the application.
 *
 * @author Angel
 *
 */
public class OpenXDataFacade {
	
	private OpenXDataFacade(){}	
	
	/**
	 * Inner <tt>class</tt> to guarantee the initialization of <tt>Dictionary variables</tt> using <tt>class initialization rules.</tt>
	 *
	 * @author Angel
	 *
	 */
	private static class DictionaryHolder{
		private static Dictionary INSTANCE = Dictionary.getDictionary("PurcformsText");
	}
	
	/**
	 * Return an instance of the <tt>Dictionary class.</tt>
	 * @return instance of the <tt>Dictionary class.</tt>
	 */
	public static Dictionary getDictionary(){
		return DictionaryHolder.INSTANCE;
	}
}
