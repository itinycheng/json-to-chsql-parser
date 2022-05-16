package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/** SQL function. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlFunction extends SqlNode {

    private String name;

    private List<SqlNode> operands;

    @Override
    public boolean isValid(SqlContext context) {
        if (operands == null || operands.size() == 0) {
            return super.isValid(context);
        }

        boolean isValidOperands =
                operands.stream()
                        .allMatch(
                                sqlNode -> {
                                    if (sqlNode instanceof SqlTable) {
                                        return false;
                                    } else {
                                        return sqlNode.isValid(context);
                                    }
                                });

        return isValidOperands && super.isValid(context);
    }
}
