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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@SuppressWarnings("ALL")
@SpringBootApplication(
    scanBasePackages = { "org.cvcoei.sistools.common", "org.cvcoei.sistools.csv.logins" },
    proxyBeanMethods = false)
public class Application {

    /**
     * Entry point.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

    @Service
    public static class Runner implements ApplicationRunner {

        @Value("${cvc.sis.sql.logins}")
        String sqlLogins;

        @Autowired
        DataSource sisDatasource;

        @Override
        public void run(ApplicationArguments args) throws Exception {
            // Create a JdbcTemplate for interacting with the SIS database
            JdbcTemplate jdbc = new JdbcTemplate(sisDatasource);

            // Execute the logins query
            jdbc
                .queryForList(sqlLogins)
                .stream()
                .forEach(System.out::println);
        }
    }

}
