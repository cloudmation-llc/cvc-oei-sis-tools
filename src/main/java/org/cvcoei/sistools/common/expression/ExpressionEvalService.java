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

package org.cvcoei.sistools.common.expression;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Spring service to provide expression parsing and helper methods to apply expressions throughout
 * an application.
 */
@Service
public class ExpressionEvalService {

    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    public Expression parse(String expression) {
        return expressionParser.parseExpression(expression);
    }

    public Object eval(Expression expression) {
        return expression.getValue();
    }

    public Object eval(Expression expression, Map properties) {
        StandardEvaluationContext context = new StandardEvaluationContext(properties);
        context.addPropertyAccessor(new MapAccessor());
        return expression.getValue(context);
    }

}
