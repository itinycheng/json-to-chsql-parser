package com.cksql.parser.snippet;

import java.util.Arrays;

import static com.cksql.parser.snippet.FunctionType.AGG_FUNC;
import static com.cksql.parser.snippet.FunctionType.COM_FUNC;

/** preset functions. */
public enum BuildInFunction {

    /** only for numbers. */
    SUM("Sum", "sum(%s)", AGG_FUNC),

    MAX("Max", "max(%s)", AGG_FUNC),

    MIN("Min", "min(%s)", AGG_FUNC),

    /** for Integer, float, Decimal. */
    AVG("Avg", "avg(%s)", AGG_FUNC),

    /** available for all types. */
    COUNT("Count", "count(%s)", AGG_FUNC),

    UNIQ_COUNT("UniqCount", "count(distinct %s)", AGG_FUNC),

    // ------------------------------------------------------------------------
    //  common function
    // ------------------------------------------------------------------------

    MILLS_TO_TIME("millsToTime", "fromUnixTimestamp64Milli(CAST(%s, 'Int64'))", COM_FUNC),

    TO_YYYYMM("toYYYYMM", "toYYYYMM(%s)", COM_FUNC),

    TO_YYYYMMDD("toYYYYMMDD", "toYYYYMMDD(%s)", COM_FUNC),

    TO_YYYYMMDDHHMMSS("toYYYYMMDDhhmmss", "toYYYYMMDDhhmmss(%s)", COM_FUNC),

    ROW_NUM("rowNum", "rowNumberInAllBlocks() as row_num", COM_FUNC),

    FORMAT_DATE_TIME("formatDateTime", "formatDateTime(%s, %s)", COM_FUNC);

    public final String name;

    public final String format;

    public final FunctionType type;

    BuildInFunction(String name, String format, FunctionType type) {
        this.name = name;
        this.format = format;
        this.type = type;
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
