package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Simple sql where. TODO: operands not null, 1=< length <= 2 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleSqlWhere extends SqlWhere {

    private SqlExpression operator;

    private SqlNode[] operands;

    public String toSQL(SqlContext context) {
        SqlColumn sqlColumn = operands[0].unwrap(SqlColumn.class);
        SqlLiteral sqlLiteral = operands.length == 2 ? operands[1].unwrap(SqlLiteral.class) : null;
        Object[] formatted = new Object[operands.length];
        formatted[0] = sqlColumn.toSQL(context);
        if (sqlLiteral != null) {
            formatted[1] = sqlLiteral.toSQL(sqlColumn.getDataType(context), operator.enableQuoting);
        }
        // replace variable in sql expression
        return String.format(operator.expression, formatted);
    }
}
