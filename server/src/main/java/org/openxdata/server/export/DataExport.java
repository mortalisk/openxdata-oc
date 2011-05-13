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
package org.openxdata.server.export;

import java.io.Writer;
import java.util.Date;

/**
 *
 * @author Jonny Heggheim
 */
public interface DataExport {

    /**
     * Writes data, collected for a particular form version, to a stream in a CSV format.
     *
     * @param printWriter the stream to write the data.
     * @param formId the unique identifier of the form version whose data we are to export. This should never be null.
     * @param fromDate the data submission date from which start the export. Supply null to include all dates.
     * @param toDate the data submission date to which to end the export. Supply null to include all dates
     * @param userId the user who submitted the data. Supply null to export data for all users.
     */
    void export(Writer printWriter, Integer formId, Date fromDate, Date toDate, Integer userId);
}
