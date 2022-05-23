package com.chsql.parser.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** basic data type. */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicDataType extends DataType {

    public BasicDataType(LogicalType logicalType) {
        super(logicalType);
    }
}
