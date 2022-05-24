package com.chsql.parser;

import com.chsql.parser.snippet.BuildInFunction;
import com.chsql.parser.snippet.SqlExpression;
import com.chsql.parser.type.LogicalType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chsql.parser.snippet.BuildInFunction.AVG;
import static com.chsql.parser.snippet.BuildInFunction.COUNT;
import static com.chsql.parser.snippet.BuildInFunction.FORMAT_DATE_TIME;
import static com.chsql.parser.snippet.BuildInFunction.LENGTH;
import static com.chsql.parser.snippet.BuildInFunction.MAX;
import static com.chsql.parser.snippet.BuildInFunction.MILLS_TO_TIME;
import static com.chsql.parser.snippet.BuildInFunction.MIN;
import static com.chsql.parser.snippet.BuildInFunction.SUM;
import static com.chsql.parser.snippet.BuildInFunction.TO_YYYYMM;
import static com.chsql.parser.snippet.BuildInFunction.TO_YYYYMMDD;
import static com.chsql.parser.snippet.BuildInFunction.TO_YYYYMMDDHHMMSS;
import static com.chsql.parser.snippet.BuildInFunction.UNIQ_COUNT;
import static com.chsql.parser.snippet.SqlExpression.BETWEEN;
import static com.chsql.parser.snippet.SqlExpression.EQ;
import static com.chsql.parser.snippet.SqlExpression.GE;
import static com.chsql.parser.snippet.SqlExpression.GT;
import static com.chsql.parser.snippet.SqlExpression.HAS_ALL;
import static com.chsql.parser.snippet.SqlExpression.HAS_ANY;
import static com.chsql.parser.snippet.SqlExpression.IS_NOT_NULL;
import static com.chsql.parser.snippet.SqlExpression.LE;
import static com.chsql.parser.snippet.SqlExpression.LT;
import static com.chsql.parser.snippet.SqlExpression.NE;

/** sql meta viewer. */
public class SqlMetaExplorer {

    public static Map<LogicalType, List<SqlExpression>> getOperators() {
        Map<LogicalType, List<SqlExpression>> result = new HashMap<>();
        for (LogicalType logicalType : LogicalType.values()) {
            result.put(logicalType, getOperators(logicalType));
        }
        return result;
    }

    public static List<SqlExpression> getOperators(LogicalType logicalType) {
        switch (logicalType) {
            case NUMBER:
            case INT:
            case FLOAT:
            case DECIMAL:
            case STRING:
            case DATETIME:
                return Arrays.asList(EQ, NE, GT, GE, LT, LE, IS_NOT_NULL, IS_NOT_NULL, BETWEEN);
            case ARRAY:
                return Arrays.asList(HAS_ANY, HAS_ALL);
            case MAP:
            case ANY:
            default:
                return Collections.emptyList();
        }
    }

    /** data type supported function list. */
    public static Map<LogicalType, List<BuildInFunction>> getFunctions() {
        Map<LogicalType, List<BuildInFunction>> result = new HashMap<>();
        for (LogicalType logicalType : LogicalType.values()) {
            result.put(logicalType, getFunctions(logicalType));
        }
        return result;
    }

    public static List<BuildInFunction> getFunctions(LogicalType logicalType) {
        switch (logicalType) {
            case NUMBER:
            case FLOAT:
            case DECIMAL:
                return Arrays.asList(SUM, MAX, MIN, AVG, COUNT, UNIQ_COUNT);
            case INT:
                return Arrays.asList(SUM, MAX, MIN, AVG, COUNT, UNIQ_COUNT, MILLS_TO_TIME);
            case DATETIME:
                return Arrays.asList(
                        MAX,
                        MIN,
                        COUNT,
                        UNIQ_COUNT,
                        TO_YYYYMM,
                        TO_YYYYMMDD,
                        TO_YYYYMMDDHHMMSS,
                        FORMAT_DATE_TIME);
            case STRING:
                return Arrays.asList(MAX, MIN, COUNT, UNIQ_COUNT);
            case ARRAY:
                return Collections.singletonList(LENGTH);
            case MAP:
            case ANY:
            default:
                return Collections.emptyList();
        }
    }
}
