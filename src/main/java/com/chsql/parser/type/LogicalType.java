package com.chsql.parser.type;

import java.util.Arrays;
import java.util.Map;

import static com.chsql.parser.common.Constant.EMPTY;
import static com.chsql.parser.common.Constant.SINGLE_QUOTE;
import static java.util.stream.Collectors.toMap;

/** logical types. */
public enum LogicalType {
    ANY(EMPTY, "any"),

    NUMBER(EMPTY, "NUMBER"),

    STRING(SINGLE_QUOTE, "STRING"),

    ARRAY(EMPTY, "ARRAY"),

    MAP(EMPTY, "MAP"),

    INT(EMPTY, "INTEGER"),

    FLOAT(EMPTY, "FLOAT"),

    DECIMAL(EMPTY, "decimal"),

    DATETIME(EMPTY, "datetime");

    public final String quote;

    public final String sqlType;

    LogicalType(String quote, String sqlType) {
        this.quote = quote;
        this.sqlType = sqlType;
    }

    private static final Map<String, LogicalType> ENUM_MAP =
            Arrays.stream(values()).collect(toMap(Enum::name, logicalType -> logicalType));

    public static LogicalType of(String type) {
        return ENUM_MAP.get(type.toUpperCase());
    }
}
