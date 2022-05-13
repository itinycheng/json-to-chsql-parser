package com.cksql.parser.model;

import com.cksql.parser.type.LogicalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.cksql.parser.common.Constant.BACK_TICK;
import static com.cksql.parser.common.Constant.DOT;
import static com.cksql.parser.common.Constant.EMPTY;

/** Sql identifier. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlColumn extends SqlNode {

    private String qualifier;

    private String[] names;

    public String toSQL(LogicalType logicalType) {
        String column;
        switch (logicalType) {
            case STRING:
            case NUMBER:
            case LIST:
                column = names[0];
                break;
            case MAP:
                column = String.join(EMPTY, names[0], "['", names[1], "']");
                break;
            default:
                throw new RuntimeException("unsupported data type:" + logicalType);
        }
        return String.join(EMPTY, qualifier, DOT, column);
    }

    public String quotedColumn(String name) {
        return String.join(EMPTY, BACK_TICK, name, BACK_TICK);
    }
}
