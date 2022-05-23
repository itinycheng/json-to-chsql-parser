package com.chsql.parser.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** map data type. */
@Data
@EqualsAndHashCode(callSuper = true)
public class MapDataType extends DataType {

    private final DataType keyDataType;

    private final DataType valueDataType;

    public MapDataType(DataType keyDataType, DataType valueDataType) {
        super(LogicalType.MAP);
        this.keyDataType = keyDataType;
        this.valueDataType = valueDataType;
    }
}
