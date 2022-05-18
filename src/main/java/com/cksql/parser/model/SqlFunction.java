package com.cksql.parser.model;

import com.cksql.parser.common.LiteralRelated;
import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.BuildInFunction;
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
    public List<SqlColumn> getColumns() {
        List<SqlColumn> columns = new ArrayList<>();
        for (SqlNode sqlNode : operands) {
            columns.addAll(sqlNode.getColumns());
        }
        return columns;
    }

    @Override
    public boolean isValid(SqlContext context) {
        if (operands == null || operands.size() == 0) {
            return true;
        }

        return operands.stream()
                .allMatch(
                        sqlNode -> {
                            if (sqlNode instanceof SqlTable) {
                                return false;
                            } else {
                                return sqlNode.isValid(context);
                            }
                        });
    }

    // TODO: currently, only support extract dataType from column.
    @Override
    public String toSQL(SqlContext context, Object... relation) {
        BuildInFunction function = BuildInFunction.of(name);
        SqlColumn sqlColumn =
                operands.stream()
                        .filter(sqlNode -> sqlNode instanceof SqlColumn)
                        .map(sqlNode -> sqlNode.unwrap(SqlColumn.class))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("SqlColumn not found"));
        LiteralRelated related = new LiteralRelated(sqlColumn.getDataType(context), true);
        List<String> operandList = new ArrayList<>();
        for (SqlNode sqlNode : operands) {
            operandList.add(sqlNode.toSQL(context, related));
        }

        return String.format(function.format, operandList.toArray());
    }
}
