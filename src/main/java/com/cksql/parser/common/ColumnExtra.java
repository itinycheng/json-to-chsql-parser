package com.cksql.parser.common;

import com.cksql.parser.type.DataType;
import lombok.Data;

import static com.cksql.parser.util.ClickHouseTypeUtil.toDataType;

/** column extra info. */
@Data
public class ColumnExtra {

    private final String name;

    private final Long tableId;

    private final String originType;

    private final DataType dataType;

    public ColumnExtra(String name, Long tableId, String originType) {
        this.name = name;
        this.tableId = tableId;
        this.originType = originType;
        this.dataType = toDataType(name, originType);
    }
}
