package com.cksql.parser.snippet;

import com.cksql.parser.type.BasicDataType;
import com.cksql.parser.type.DataType;

import java.util.Arrays;

import static com.cksql.parser.snippet.FunctionType.AGG_FUNC;
import static com.cksql.parser.snippet.FunctionType.COM_FUNC;
import static com.cksql.parser.type.LogicalType.ANY;
import static com.cksql.parser.type.LogicalType.DATETIME;
import static com.cksql.parser.type.LogicalType.INT;
import static com.cksql.parser.type.LogicalType.NUMBER;

/** preset functions. */
public enum BuildInFunction {

    /** only for numbers. */
    SUM("Sum", "sum(%s)", AGG_FUNC, new BasicDataType(NUMBER)),

    MAX("Max", "max(%s)", AGG_FUNC, new BasicDataType(ANY)),

    MIN("Min", "min(%s)", AGG_FUNC, new BasicDataType(ANY)),

    /** for Integer, float, Decimal. */
    AVG("Avg", "avg(%s)", AGG_FUNC, new BasicDataType(NUMBER)),

    /** available for all types. */
    COUNT("Count", "count(%s)", AGG_FUNC, new BasicDataType(INT)),

    UNIQ_COUNT("UniqCount", "count(distinct %s)", AGG_FUNC, new BasicDataType(INT)),

    // ------------------------------------------------------------------------
    //  common function
    // ------------------------------------------------------------------------

    MILLS_TO_TIME(
            "millsToTime",
            "fromUnixTimestamp64Milli(CAST(%s, 'Int64'))",
            COM_FUNC,
            new BasicDataType(DATETIME)),

    TO_YYYYMM("toYYYYMM", "toYYYYMM(%s)", COM_FUNC, new BasicDataType(INT)),

    TO_YYYYMMDD("toYYYYMMDD", "toYYYYMMDD(%s)", COM_FUNC, new BasicDataType(INT)),

    TO_YYYYMMDDHHMMSS("toYYYYMMDDhhmmss", "toYYYYMMDDhhmmss(%s)", COM_FUNC, new BasicDataType(INT)),

    ROW_NUM("rowNum", "rowNumberInAllBlocks() as row_num", COM_FUNC, new BasicDataType(INT)),

    FORMAT_DATE_TIME(
            "formatDateTime", "formatDateTime(%s, %s)", COM_FUNC, new BasicDataType(DATETIME)),

    LENGTH("length", "length(%s)", COM_FUNC, new BasicDataType(INT));

    public final String name;

    public final String format;

    public final FunctionType type;

    public final DataType resultType;

    BuildInFunction(String name, String format, FunctionType type, DataType resultType) {
        this.name = name;
        this.format = format;
        this.type = type;
        this.resultType = resultType;
    }

    public boolean isAggFunc() {
        return type == AGG_FUNC;
    }

    public static BuildInFunction of(final String name) {
        return Arrays.stream(values())
                .filter(expr -> expr.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "can not find a suitable sql function, name:" + name));
    }
}
