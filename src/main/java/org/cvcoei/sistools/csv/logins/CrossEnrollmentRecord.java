/*
 * Copyright 2020 California Community Colleges Chancellor's Office
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cvcoei.sistools.csv.logins;

import com.google.common.base.MoreObjects;
import com.opencsv.bean.CsvBindByName;

/**
 * Immutable record representing a cross enrollment record received from an external input such as an SIS
 * database or flat file delivered by a CVC integration partner. Includes OpenCSV annotations for use with
 * reading from a local input file.
 */
public class CrossEnrollmentRecord {

    @CsvBindByName(column = "canvasRootAccount", required = true)
    private String canvasRootAccount;

    @CsvBindByName(column = "homeSISUserId", required = true)
    private String homeCollegeId;

    @CsvBindByName(column = "canvasLoginId", required = true)
    private String homeCollegeLoginId;

    @CsvBindByName(column = "studentid", required = true)
    private String teachingCollegeId;

    public CrossEnrollmentRecord() {
    }

    public CrossEnrollmentRecord(String canvasRootAccount, String homeCollegeId, String homeCollegeLoginId, String teachingCollegeId) {
        this.canvasRootAccount = canvasRootAccount;
        this.homeCollegeId = homeCollegeId;
        this.homeCollegeLoginId = homeCollegeLoginId;
        this.teachingCollegeId = teachingCollegeId;
    }

    public String getCanvasRootAccount() {
        return canvasRootAccount;
    }

    public void setCanvasRootAccount(String canvasRootAccount) {
        this.canvasRootAccount = canvasRootAccount;
    }

    public String getHomeCollegeId() {
        return homeCollegeId;
    }

    public void setHomeCollegeId(String homeCollegeId) {
        this.homeCollegeId = homeCollegeId;
    }

    public String getHomeCollegeLoginId() {
        return homeCollegeLoginId;
    }

    public void setHomeCollegeLoginId(String homeCollegeLoginId) {
        this.homeCollegeLoginId = homeCollegeLoginId;
    }

    public String getTeachingCollegeId() {
        return teachingCollegeId;
    }

    public void setTeachingCollegeId(String teachingCollegeId) {
        this.teachingCollegeId = teachingCollegeId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("canvasRootAccount", canvasRootAccount)
            .add("homeCollegeId", homeCollegeId)
            .add("homeCollegeLoginId", homeCollegeLoginId)
            .add("teachingCollegeId", teachingCollegeId)
            .toString();
    }

}
