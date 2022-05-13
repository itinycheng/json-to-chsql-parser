package com.cksql.parser.model;

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
    public boolean isValid() {
        if (operands == null || operands.size() == 0) {
            return super.isValid();
        }

        boolean isValidOperands =
                operands.stream()
                        .allMatch(
                                sqlNode -> {
                                    if (sqlNode instanceof SqlTable) {
                                        return false;
                                    } else {
                                        return sqlNode.isValid();
                                    }
                                });

        return isValidOperands && super.isValid();
    }
}
