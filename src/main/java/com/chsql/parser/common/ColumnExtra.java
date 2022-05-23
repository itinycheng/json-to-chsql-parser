package com.chsql.parser.common;

import com.chsql.parser.type.DataType;
import com.chsql.parser.util.ClickHouseUtil;
import lombok.Data;

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
        this.dataType = ClickHouseUtil.toDataType(name, originType);
    }
}
