package com.cksql.parser.util;

import com.cksql.parser.type.ArrayDataType;
import com.cksql.parser.type.BasicDataType;
import com.cksql.parser.type.DataType;
import com.cksql.parser.type.LogicalType;
import com.cksql.parser.type.MapDataType;
import ru.yandex.clickhouse.response.ClickHouseColumnInfo;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Type utils. */
public class ClickHouseUtil {

    private static final Pattern INTERNAL_TYPE_PATTERN = Pattern.compile(".*?\\((?<type>.*)\\)");

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