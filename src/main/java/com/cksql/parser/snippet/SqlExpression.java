package com.cksql.parser.snippet;

import java.util.Arrays;
import java.util.List;

/** sql expression. */
public enum SqlExpression {
    /** sql expression. */
    AND(" %s AND %s "),
    OR(" %s OR %s "),
    IN(" %s IN (%s) "),
    NOT_IN(" %s NOT IN (%s) "),
    LIKE(" %s LIKE %s "),
    NOT_LIKE(" %s NOT LIKE %s "),
    EQ(" %s = %s "),
    NE(" %s <> %s "),
    GT(" %s > %s "),
    GE(" %s >= %s "),
    LT(" %s < %s "),
    LE(" %s <= %s "),
    IS_NULL(" %s IS NULL "),
    IS_NOT_NULL(" %s IS NOT NULL "),
    EXISTS(" EXISTS (%s) "),
    BETWEEN(" %s BETWEEN %s AND %s "),
    ASC(" ORDER BY %s ASC "),
    DESC(" ORDER BY %s DESC ");

    public static final List<SqlExpression> SUPPORT_MULTI_INPUT_PARAMETER =
            Arrays.asList(IN, NOT_IN);

    public final String expression;

    SqlExpression(final String expression) {
        this.expression = expression;
    }

    public static SqlExpression of(final String name) {
        return Arrays.stream(values())
                .filter(expr -> expr.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("can not find a suitable sql expression."));
    }

    public boolean isSupportMultiParameter() {
        return SUPPORT_MULTI_INPUT_PARAMETER.stream().anyMatch(this::equals);
    }
}
