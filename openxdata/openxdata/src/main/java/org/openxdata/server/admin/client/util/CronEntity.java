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
package org.openxdata.server.admin.client.util;

/**
 * Entities in the cron expression. The entities are differentiated by the type
 * flag.
 * 
 * @author Ronald.K
 */
public class CronEntity
{

    private final int MIN;
    private final int MAX;
    private int[] mValues = null;
    private CronType type;
    private int repeat = -1;

    public CronEntity(String param, int min, int max, CronEntity.CronType type)
    {
        this.MIN = min;
        this.MAX = max;
        this.type = type;
        if (!param.equals("*") && !param.equals("?"))
            this.parseEntity(param);

    }
    
    
    /**
     * Parses the entity string and constructs an array of all the possible
     * values the the expression matches e.g "10-12,14,15" = {10,11,12,14,15}
     * @param param entity string
     */
    private void parseEntity(String param)
    {

        String[] paramarray;

        if (param.indexOf(",") != -1) {
            paramarray = param.split(",");
        } else {
            paramarray = new String[]{param};
        }

        StringBuffer rangeitems = new StringBuffer();

        //convert all ranges to standard cron expressions separated by commas
        //e.g 10-12 is made 10,11,12,
        for (int i = 0; i < paramarray.length; i++) {
            if (paramarray[i].indexOf("/") != -1) {

                //set the repetition value
                repeat = Integer.parseInt(paramarray[i].substring(paramarray[i].indexOf("/") + 1));

                int start = Integer.parseInt(paramarray[i].substring(0,
                        paramarray[i].indexOf("/")));

                rangeitems.append(start + ",");

                for (int a = start; a <= this.MAX - repeat;) {
                    a = a + repeat;
                    rangeitems.append(a + ",");
                }

            } else {

                if (paramarray[i].equals("*")) {
                    rangeitems.append(fillRange(this.MIN + "-" + this.MAX));
                } else {
                    rangeitems.append(fillRange(paramarray[i]));
                }
            }
        }

        String[] values = rangeitems.toString().split(",");
        mValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            String string = values[i];
            mValues[i] = Integer.parseInt(string);
        }
    }

    private static String fillRange(String range)
    {
        // split by "-"
        if (range.indexOf("-") == -1) {
            return range + ",";
        }

        String[] rangearray = range.split("-");
        StringBuffer result = new StringBuffer();
        for (int i = Integer.parseInt(rangearray[0]); i <= Integer.parseInt(rangearray[1]); i++) {
            result.append(i + ",");
        }
        return result.toString();
    }

    /**
     * Returns the type of cron entity
     * @return the type of cron entity
     */
    public CronType getType()
    {
        return type;
    }

    /**
     * Returns the repeating number
     * @return -1 if no repeating value exists
     */
    public int getRepeat()
    {
        return repeat;
    }

    /**
     * Returns values or entries in the cron expression
     * @return null if the expression is a *
     */
    public int[] getValues()
    {
        return mValues;
    }

    public enum CronType
    {

        SECOND, MINUTE, HOUR, DAY_OF_MONTH, MONTH, DAY_OF_WEEK
    }
    public static CronType SECOND = CronType.SECOND;
    public static CronType MINUTE = CronType.MINUTE;
    public static CronType HOUR = CronType.HOUR;
    public static CronType DAY_OF_MONTH = CronType.DAY_OF_MONTH;
    public static CronType MONTH = CronType.MONTH;
    public static CronType DAY_OF_WEEK = CronType.DAY_OF_WEEK;
}
 