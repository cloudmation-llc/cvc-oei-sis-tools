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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of a record source for the PeopleSoft SIS which queries the Oracle database directly to
 * read cross-enrollment records from a staging table provided by an external integration partner.
 */
@Service
@ConditionalOnProperty(
    value="cvc.sis.type",
    havingValue = "peoplesoft")
public class PeoplesoftCrossEnrollmentRecordSource extends CrossEnrollmentRecordSource {

    @Override
    public List<CrossEnrollmentRecord> getRecords() {
        throw new RuntimeException("Not yet implemented");
    }

}
