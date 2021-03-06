package com.chsql.parser.enums;

import com.chsql.parser.type.ArrayDataType;
import com.chsql.parser.type.BasicDataType;
import com.chsql.parser.type.DataType;
import com.chsql.parser.type.LogicalType;

import java.util.Arrays;

/** preset functions. */
public enum BuildInFunction {

    /** only for numbers. */
    SUM("Sum", "sum(%s)", FunctionType.AGG_FUNC, new BasicDataType(LogicalType.NUMBER)),

    MAX("Max", "max(%s)", FunctionType.AGG_FUNC, new BasicDataType(LogicalType.ANY)),

    MIN("Min", "min(%s)", FunctionType.AGG_FUNC, new BasicDataType(LogicalType.ANY)),

    /** for Integer, float, Decimal. */
    AVG("Avg", "avg(%s)", FunctionType.AGG_FUNC, new BasicDataType(LogicalType.NUMBER)),

    /** available for all types. */
    COUNT("Count", "count(%s)", FunctionType.AGG_FUNC, new BasicDataType(LogicalType.INT)),

    UNIQ_COUNT(
            "UniqCount",
            "count(distinct %s)",
            FunctionType.AGG_FUNC,
            new BasicDataType(LogicalType.INT)),

    // ------------------------------------------------------------------------
    //  common function
    // ------------------------------------------------------------------------

    MILLS_TO_TIME(
            "millsToTime",
            "fromUnixTimestamp64Milli(CAST(%s, 'Int64'))",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.DATETIME)),

    TO_YEAR("toYear", "toYear(%s)", FunctionType.COM_FUNC, new BasicDataType(LogicalType.INT)),

    TO_YEAR_WEEK(
            "toYearWeek",
            "toYearWeek(%s, 1)",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.INT)),

    TO_YYYYMM(
            "toYYYYMM", "toYYYYMM(%s)", FunctionType.COM_FUNC, new BasicDataType(LogicalType.INT)),

    TO_YYYYMMDD(
            "toYYYYMMDD",
            "toYYYYMMDD(%s)",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.INT)),

    TO_YYYYMMDDHHMMSS(
            "toYYYYMMDDhhmmss",
            "toYYYYMMDDhhmmss(%s)",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.INT)),

    TODAY("today", "today()", FunctionType.COM_FUNC, new BasicDataType(LogicalType.DATETIME)),

    ROW_NUM(
            "rowNum",
            "rowNumberInAllBlocks() as row_num",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.INT)),

    FORMAT_DATE_TIME(
            "formatDateTime",
            "formatDateTime(%s, %s)",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.DATETIME)),

    LENGTH("length", "length(%s)", FunctionType.COM_FUNC, new BasicDataType(LogicalType.INT)),

    MAP_LENGTH(
            "mapLength",
            "length(mapKeys(%s))",
            FunctionType.COM_FUNC,
            new BasicDataType(LogicalType.INT)),

    MAP_KEYS("mapKeys", "mapKeys(%s)", FunctionType.COM_FUNC, new ArrayDataType(null)),

    MAP_VALUES("mapValues", "mapValues(%s)", FunctionType.COM_FUNC, new ArrayDataType(null)),
    ;

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
        return type == FunctionType.AGG_FUNC;
    }

    public static BuildInFunction of(final String name) {
        return Arrays.stream(values())
                .filter(expr -> expr.name.equalsIgnoreCase(name) || expr.name().equals(name))
                .findFirst()
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "can not find a suitable sql function, name:" + name));
    }
}
