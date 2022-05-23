package com.chsql.parser.snippet;

import java.util.Arrays;

/** sql expression. */
public enum SqlExpression {
    /** sql expression. */
    AND(" %s AND %s ", true),
    OR(" %s OR %s ", true),
    IN(" %s IN (%s) ", true),
    NOT_IN(" %s NOT IN (%s) ", true),
    LIKE(" %s LIKE %s ", true),
    NOT_LIKE(" %s NOT LIKE %s ", true),
    EQ(" %s = %s ", true),
    NE(" %s <> %s ", true),
    GT(" %s > %s ", true),
    GE(" %s >= %s ", true),
    LT(" %s < %s ", true),
    LE(" %s <= %s ", true),
    IS_NULL(" %s IS NULL ", true),
    IS_NOT_NULL(" %s IS NOT NULL ", true),
    EXISTS(" EXISTS (%s) ", true),
    BETWEEN(" %s BETWEEN %s AND %s ", true),
    ASC(" ORDER BY %s ASC ", true),
    DESC(" ORDER BY %s DESC ", true),

    /** Array only. */
    HAS_ANY(" hasAny(%s, [%s]) ", true),
    HAS_ALL(" hasAll(%s, [%s]) ", true);

    public final String expression;

    public final boolean enableQuoting;

    SqlExpression(final String expression, boolean enableQuoting) {
        this.expression = expression;
        this.enableQuoting = enableQuoting;
    }

    public static SqlExpression of(final String name) {
        return Arrays.stream(values())
                .filter(expr -> expr.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("can not find a suitable sql expression."));
    }
}
