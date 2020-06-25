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

import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.cvcoei.sistools.common.expression.ExpressionEvalService;
import org.cvcoei.sistools.common.json.JsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Spring service to provide HTTP helper methods for executing client requests, and higher level operations
 * such as continually polling an API within a defined period of time.
 */
@Log4j2
@Service
public class HttpApiService {

    @Autowired
    ExpressionEvalService expressionEvalService;

    @Autowired
    JsonService jsonService;

    @Autowired
    OkHttpClient httpClient;

    /**
     * Execute an HTTP request.
     * @param request An OkHttp request specifying the request to make
     * @return Assumes the response to be JSON, and returns a Map
     */
    public Map<String, Object> call(Request request){
        try (Response response = httpClient.newCall(request).execute()) {
            String jsonResponse = Objects.requireNonNull(response.body()).string();
            return jsonService.toMap(jsonResponse);
        }
        catch(IOException ioException) {
            // Rethrow unchecked
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Execute an HTTP request to download a file, and process each line through using custom logic.
     * @param request An OkHttp request specifying the request to make
     * @param lineHandler Handler function to process each line
     */
    public void fetchLines(Request request, Consumer<String> lineHandler) {
        try (Response response = httpClient.newCall(request).execute()) {
            Reader responseReader = Objects.requireNonNull(response.body()).charStream();
            try (BufferedReader buffer = new BufferedReader(responseReader)) {
                String line;
                while ((line = buffer.readLine()) != null) {
                    lineHandler.accept(line);
                }
            }
        }
        catch(IOException ioException) {
            // Rethrow unchecked
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Continually poll an API until a specific condition in the response payload is met. Currently has a hard
     * timeout set of 30 minutes so that this method does not run forever, particularly if a program is
     * configured as a cron job.
     * @param request An OkHttp request specifying the request to make
     * @param pollingInterval How often should the API be polled
     * @param watchExpression A Spring expression to check the response body. Truthy results will end the poll
     * @return The last API response when the poll terminates.
     */
    public Map<String, Object> poll(Request request, Duration pollingInterval, String watchExpression) {
        // Parse the expression used to determine when to exit the poll
        final Expression responseExpression = expressionEvalService.parse(watchExpression);

        // Create a Mono which can be continually queried until we meet our condition
        return Mono.defer(() -> {
            // Execute API request
            return Mono.just(call(request));
        })

        .repeatWhen(Repeat
            .times(100)
            .fixedBackoff(pollingInterval)
            .doOnRepeat(context -> log.info("Polling API {}", request.url())))

        .takeUntil(response -> expressionEvalService
            .eval(responseExpression, response)
            .equals(Boolean.TRUE))

        // Wait for a maximum of 30 minutes for the file to be processed so that this program
        // does not hang indefinitely.
        .blockLast(Duration.ofMinutes(30));
    }

}
