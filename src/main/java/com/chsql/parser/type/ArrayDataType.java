package com.chsql.parser.type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** Array data type. */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArrayDataType extends DataType {

    private final DataType elementDataType;

    public ArrayDataType(DataType elementDataType) {
        super(LogicalType.ARRAY);
        this.elementDataType = elementDataType;
    }
}
