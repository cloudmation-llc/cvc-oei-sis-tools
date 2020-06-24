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
