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
 * Parses Cron expressions
 * @author Ronald.K
 */
public class CronExpressionParser {

    private final CronEntity mSecond;
    private final CronEntity mMinute;
    private final CronEntity mHour;
    private final CronEntity mDayOfMonth;
    private final CronEntity mMonth;
    private final CronEntity mDayOfWeek;

    public CronExpressionParser(String line) {
        String[] split   = line.split(" ");
        mSecond          = new CronEntity(split[0], 0, 59, CronEntity.SECOND);
        mMinute          = new CronEntity(split[1], 0, 59, CronEntity.MINUTE);
        mHour            = new CronEntity(split[2], 0, 23, CronEntity.HOUR);
        mDayOfMonth      = new CronEntity(split[3], 1, 31, CronEntity.DAY_OF_MONTH);
        mMonth           = new CronEntity(split[4], 1, 12, CronEntity.MONTH);
        mDayOfWeek       = new CronEntity(split[5], 1, 07, CronEntity.DAY_OF_WEEK);
    }

    public CronEntity getDayOfMonth() {
        return mDayOfMonth;
    }

    public CronEntity getDayOfWeek() {
        return mDayOfWeek;
    }

    public CronEntity getHour() {
        return mHour;
    }

    public CronEntity getMinute() {
        return mMinute;
    }

    public CronEntity getMonth() {
        return mMonth;
    }

    public CronEntity getSecond() {
        return mSecond;
    }
}
