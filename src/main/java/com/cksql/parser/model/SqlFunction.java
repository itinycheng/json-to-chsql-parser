package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.cksql.parser.common.Constant.UNDERSCORE;

/** SQL function. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlFunction extends SqlNode {

    private String name;

    private List<SqlNode> operands;

    @Override
    public String ident() {
        List<String> subIdentList = new ArrayList<>(operands.size() + 1);
        subIdentList.add(name.toLowerCase());
        for (SqlNode sqlNode : operands) {
            if (sqlNode instanceof SqlColumn || sqlNode instanceof SqlFunction) {
                subIdentList.add(sqlNode.ident());
            }
        }
        return String.join(UNDERSCORE, subIdentList);
    }

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

    public List<SqlColumn> getAllColumns() {
        List<SqlColumn> columns = new ArrayList<>();
        for (SqlNode sqlNode : operands) {
            if (sqlNode instanceof SqlFunction) {
                columns.addAll(sqlNode.unwrap(SqlFunction.class).getAllColumns());
            } else if (sqlNode instanceof SqlColumn) {
                columns.add(sqlNode.unwrap(SqlColumn.class));
            }
        }
        return columns;
    }
}
