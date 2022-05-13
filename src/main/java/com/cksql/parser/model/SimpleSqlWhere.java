package com.cksql.parser.model;

import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Simple sql where. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleSqlWhere extends SqlWhere {

    private SqlExpression operator;

    private SqlColumn column;

    private String[] operands;

    public String toSQL(SqlContext context) {
        SqlExpression operatorExpr = operator;
        // fill operands to object array
        Object[] formatted = new Object[this.getOperands().length + 1];
        formatted[0] = column.toSQL(null);
        return null;
    }
}
