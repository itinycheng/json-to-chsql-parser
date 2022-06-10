package com.chsql.parser;

import com.chsql.parser.enums.BuildInFunction;
import com.chsql.parser.enums.SqlExpression;
import com.chsql.parser.type.LogicalType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chsql.parser.enums.BuildInFunction.AVG;
import static com.chsql.parser.enums.BuildInFunction.COUNT;
import static com.chsql.parser.enums.BuildInFunction.FORMAT_DATE_TIME;
import static com.chsql.parser.enums.BuildInFunction.LENGTH;
import static com.chsql.parser.enums.BuildInFunction.MAX;
import static com.chsql.parser.enums.BuildInFunction.MILLS_TO_TIME;
import static com.chsql.parser.enums.BuildInFunction.MIN;
import static com.chsql.parser.enums.BuildInFunction.SUM;
import static com.chsql.parser.enums.BuildInFunction.TO_YEAR;
import static com.chsql.parser.enums.BuildInFunction.TO_YEAR_WEEK;
import static com.chsql.parser.enums.BuildInFunction.TO_YYYYMM;
import static com.chsql.parser.enums.BuildInFunction.TO_YYYYMMDD;
import static com.chsql.parser.enums.BuildInFunction.TO_YYYYMMDDHHMMSS;
import static com.chsql.parser.enums.BuildInFunction.UNIQ_COUNT;
import static com.chsql.parser.enums.SqlExpression.BETWEEN;
import static com.chsql.parser.enums.SqlExpression.EQ;
import static com.chsql.parser.enums.SqlExpression.GE;
import static com.chsql.parser.enums.SqlExpression.GT;
import static com.chsql.parser.enums.SqlExpression.HAS_ALL;
import static com.chsql.parser.enums.SqlExpression.HAS_ANY;
import static com.chsql.parser.enums.SqlExpression.IS_NOT_NULL;
import static com.chsql.parser.enums.SqlExpression.LE;
import static com.chsql.parser.enums.SqlExpression.LT;
import static com.chsql.parser.enums.SqlExpression.NE;

/** sql meta viewer. */
public class SqlExplorer {

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
                        TO_YEAR,
                        TO_YEAR_WEEK,
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
