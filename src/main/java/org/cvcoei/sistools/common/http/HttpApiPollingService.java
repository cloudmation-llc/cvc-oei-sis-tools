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

package org.cvcoei.sistools.common.http;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.cvcoei.sistools.common.expression.ExpressionEvalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.time.Duration;
import java.util.Map;

/**
 * Spring service to provide HTTP polling methods such as (but not limited to) monitoring
 * long-running Canvas operations.
 */
@SuppressWarnings({"ALL"})
@Log4j2
@Service
public class HttpApiPollingService {

    @Autowired
    ExpressionEvalService expressionEvalService;

    @Autowired
    Gson gson;

    @Autowired
    OkHttpClient httpClient;

    public Map pollJsonApi(Request request, Duration pollingInterval, String watchExpression) {
        // Create the repeat rule for polling the API
        final Repeat repeatRule = Repeat
            .times(100)
            .fixedBackoff(pollingInterval);

        // Parse the expression used to determine when to exit the poll
        final Expression responseExpression = expressionEvalService.parse(watchExpression);

        // Create a Mono which can be continually queried until we meet our condition
        return (Map) Mono.defer(() -> {
            // Execute API request
            try (Response response = httpClient.newCall(request).execute()) {
                // Parse JSON response
                Map parsedResponse = gson.fromJson(response.body().string(), Map.class);
                return Mono.just(parsedResponse);
            }
            catch(Exception exception) {
                return Mono.error(exception);
            }
        })

        // Apply the repeat rule
        .repeatWhen(repeatRule)

        // Apply a condition to end the polling when the expression evaluates to true
        .takeUntil(rawResponse -> {
            Map response = (Map) rawResponse;
            log.info("Inspecting workflow state {}", response.get("workflow_state"));
            return expressionEvalService
                .eval(responseExpression, response)
                .equals(Boolean.TRUE);
        })

        // TODO: Consider applying a timeout so that this does not run indefinitely
        .blockLast();
    }

}
