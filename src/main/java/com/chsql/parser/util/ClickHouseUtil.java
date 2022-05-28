package com.chsql.parser.util;

import com.chsql.parser.common.DistributedEngineFull;
import com.chsql.parser.type.ArrayDataType;
import com.chsql.parser.type.BasicDataType;
import com.chsql.parser.type.DataType;
import com.chsql.parser.type.LogicalType;
import com.chsql.parser.type.MapDataType;
import ru.yandex.clickhouse.response.ClickHouseColumnInfo;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Type utils. */
public class ClickHouseUtil {

    private static final Pattern INTERNAL_TYPE_PATTERN = Pattern.compile(".*?\\((?<type>.*)\\)");

    public static final Pattern DISTRIBUTED_ENGINE_FULL_PATTERN =
            Pattern.compile(
                    "Distributed\\((?<cluster>[a-zA-Z_][0-9a-zA-Z_]*),\\s*(?<database>[a-zA-Z_][0-9a-zA-Z_]*),\\s*(?<table>[a-zA-Z_][0-9a-zA-Z_]*)");

    public static DistributedEngineFull parseEngineFull(String engineFull) {
        Matcher matcher = DISTRIBUTED_ENGINE_FULL_PATTERN.matcher(engineFull.replace("'", ""));
        if (matcher.find()) {
            String cluster = matcher.group("cluster");
            String database = matcher.group("database");
            String table = matcher.group("table");
            return DistributedEngineFull.of(cluster, database, table);
        } else {
            return null;
        }
    }

    /** Convert clickhouse data type to internal data type. */
    public static DataType toDataType(String columnName, String originType) {
        ClickHouseColumnInfo columnInfo =
                ClickHouseColumnInfo.parse(originType, columnName, TimeZone.getDefault());
        return toDataType(columnInfo);
    }

    private static DataType toDataType(ClickHouseColumnInfo clickHouseColumnInfo) {
        switch (clickHouseColumnInfo.getClickHouseDataType()) {
            case Int8:
            case Int16:
            case UInt8:
            case Int32:
            case UInt16:
            case IntervalYear:
            case IntervalMonth:
            case IntervalWeek:
            case IntervalDay:
            case IntervalHour:
            case IntervalQuarter:
            case IntervalMinute:
            case IntervalSecond:
            case Int64:
            case UInt32:
            case Int128:
            case Int256:
            case UInt64:
            case UInt128:
            case UInt256:
                return new BasicDataType(LogicalType.INT);
            case Float32:
            case Float64:
                return new BasicDataType(LogicalType.FLOAT);
            case Decimal:
            case Decimal32:
            case Decimal64:
            case Decimal128:
            case Decimal256:
                return new BasicDataType(LogicalType.DECIMAL);
            case String:
            case Enum8:
            case Enum16:
            case FixedString:
            case IPv4:
            case IPv6:
            case UUID:
                return new BasicDataType(LogicalType.STRING);
            case Date:
            case DateTime:
            case DateTime32:
            case DateTime64:
                return new BasicDataType(LogicalType.DATETIME);
            case Array:
                String arrayBaseType =
                        getInternalClickHouseType(clickHouseColumnInfo.getOriginalTypeName());
                ClickHouseColumnInfo arrayBaseColumnInfo =
                        ClickHouseColumnInfo.parse(
                                arrayBaseType,
                                clickHouseColumnInfo.getColumnName() + ".array_base",
                                clickHouseColumnInfo.getTimeZone());
                return new ArrayDataType(toDataType(arrayBaseColumnInfo));
            case Map:
                return new MapDataType(
                        toDataType(clickHouseColumnInfo.getKeyInfo()),
                        toDataType(clickHouseColumnInfo.getValueInfo()));
            case Tuple:
            case Nested:
            case AggregateFunction:
            default:
                throw new UnsupportedOperationException(
                        "Unsupported type:" + clickHouseColumnInfo.getClickHouseDataType());
        }
    }

    private static String getInternalClickHouseType(String clickHouseTypeLiteral) {
        Matcher matcher = INTERNAL_TYPE_PATTERN.matcher(clickHouseTypeLiteral);
        if (matcher.find()) {
            return matcher.group("type");
        } else {
            throw new RuntimeException(
                    String.format("No content found in the bucket of '%s'", clickHouseTypeLiteral));
        }
    }
}
