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
package org.openxdata.server.dao;

import java.util.List;

import org.openxdata.server.admin.model.StudyDef;

/**
 *
 * @author Jonny Heggheim
 */
public interface StudyDAO extends BaseDAO<StudyDef>{

    /**
     * Gets a list of studies.
     *
     * @return the study list
     */
    List<StudyDef> getStudies();

    /**
     * Saves a study to the database.
     *
     * @param studyDef the study to save.
     */
    void saveStudy(StudyDef studyDef);

    /**
     * Deletes a study from the database.
     *
     * @param studyDef the study to delete.
     */
    void deleteStudy(StudyDef studyDef);

	/**
	 * Gets the key of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the key of the study or null if not found
	 */
	String getStudyKey(Integer studyId);

	/**
	 * Gets the name of a study with a given id.
	 * 
	 * @param studyId
	 *            the identifier of the study.
	 * @return the name of the study or null if not found
	 */
	String getStudyName(int studyId);
}
