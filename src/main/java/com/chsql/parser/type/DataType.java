package com.chsql.parser.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** data type. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DataType implements Serializable {

    protected LogicalType logicalType;
}
