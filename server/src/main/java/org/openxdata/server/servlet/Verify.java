/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at <p>
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  </p>
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.servlet;

/**
 *
 * @author Jonny Heggheim
 */
class Verify {

    public static boolean isNullOrEmpty(String string) {
        if (string == null) {
            return true;
        }

        return string.trim().length() == 0;
    }

    public static boolean isValidId(String id) {
        if (isNullOrEmpty(id)) {
            return false;
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException idIsNotANumber) {
            return false;
        }

        return true;
    }

    public static boolean isValidDate(String date) {
        return isValidLong(date);
    }

    public static boolean isValidLong(String number) {
        if(isNullOrEmpty(number)) {
            return false;
        }
        try {
            Long.parseLong(number);
        } catch (NumberFormatException isNotANumber) {
            return false;
        }

        return true;
    }

    public static boolean isValidType(String type) {
        return !isNullOrEmpty(type);
    }
}
