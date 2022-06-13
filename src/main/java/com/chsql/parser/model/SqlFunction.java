package com.chsql.parser.model;

import com.chsql.parser.SqlValidator;
import com.chsql.parser.common.LiteralRelated;
import com.chsql.parser.common.SqlContext;
import com.chsql.parser.enums.BuildInFunction;
import com.chsql.parser.type.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.chsql.parser.common.Constant.DOLLAR_SYMBOL;
import static com.chsql.parser.common.Constant.UNDERSCORE;

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
        BuildInFunction function = BuildInFunction.of(name);
        if (function.isAggFunc()) {
            subIdentList.add(name.toLowerCase() + DOLLAR_SYMBOL);
        } else {
            subIdentList.add(name.toLowerCase());
        }

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
    public boolean validate(SqlValidator validator, SqlContext context) {
        return validator.validateFunction(this);
    }

    @Override
    public String toSQL(SqlContext context, Object... relation) {
        BuildInFunction function = BuildInFunction.of(name);
        List<String> operandList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(operands)) {
            DataType dataType = null;
            for (SqlNode sqlNode : operands) {
                if (sqlNode instanceof SqlColumn) {
                    SqlColumn sqlColumn = sqlNode.unwrap(SqlColumn.class);
                    dataType = sqlColumn.getDataType(context);
                    break;
                } else if (sqlNode instanceof SqlFunction) {
                    String name = sqlNode.unwrap(SqlFunction.class).getName();
                    dataType = BuildInFunction.of(name).resultType;
                    break;
                }
            }

            if (dataType == null) {
                throw new RuntimeException("Data type is not found");
            }

            LiteralRelated related = new LiteralRelated(dataType, true);
            for (SqlNode sqlNode : operands) {
                operandList.add(sqlNode.toSQL(context, related));
            }
        }
        return String.format(function.format, operandList.toArray());
    }
}
