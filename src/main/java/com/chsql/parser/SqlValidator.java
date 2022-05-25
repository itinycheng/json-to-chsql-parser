package com.chsql.parser;

import com.chsql.parser.common.SqlContext;
import com.chsql.parser.model.CompositeSqlWhere;
import com.chsql.parser.model.SimpleSqlWhere;
import com.chsql.parser.model.SqlColumn;
import com.chsql.parser.model.SqlFunction;
import com.chsql.parser.model.SqlLimit;
import com.chsql.parser.model.SqlLiteral;
import com.chsql.parser.model.SqlNode;
import com.chsql.parser.model.SqlOrderBy;
import com.chsql.parser.model.SqlSelect;
import com.chsql.parser.model.SqlTable;
import com.chsql.parser.model.SqlWhere;
import com.chsql.parser.snippet.BuildInFunction;
import com.chsql.parser.snippet.SqlExpression;
import com.chsql.parser.type.DataType;
import com.chsql.parser.type.MapDataType;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.chsql.parser.snippet.SqlExpression.ASC;
import static com.chsql.parser.snippet.SqlExpression.DESC;

/**
 * sql validate.
 */
@AllArgsConstructor
public class SqlValidator {

    private final SqlContext context;

    public boolean validate(SqlSelect sqlSelect) {
        return validateSelect(sqlSelect.getSelect())
                && validateFrom(sqlSelect.getFrom())
                && validateWhere(sqlSelect.getWhere())
                && validateOrderBy(sqlSelect.getOrderBy())
                && validateLimit(sqlSelect.getLimit());
    }

    public boolean validateSelect(List<SqlNode> select) {
        return CollectionUtils.isNotEmpty(select)
                && select.stream().allMatch(sqlNode -> sqlNode.validate(this, context));
    }

    public boolean validateFrom(List<SqlTable> from) {
        return CollectionUtils.isNotEmpty(from)
                && from.stream().allMatch(sqlTable -> sqlTable.validate(this, context));
    }

    public boolean validateWhere(SqlWhere where) {
        if (where == null) {
            return true;
        }

        return where.validate(this, context);
    }

    public boolean validateOrderBy(SqlOrderBy orderBy) {
        if (orderBy == null) {
            return true;
        }

        SqlExpression type = orderBy.getType();
        if (type != ASC && type != DESC) {
            return false;
        }

        List<SqlNode> items = orderBy.getItems();
        return CollectionUtils.isNotEmpty(items)
                && items.stream().allMatch(sqlNode -> sqlNode.validate(this, context));
    }

    public boolean validateLimit(SqlLimit limit) {
        if (limit == null) {
            return true;
        }

        if (limit.getOffset() < 0 || limit.getRowCount() < 1) {
            return false;
        }

        return !limit.isHasOther() || limit.getOffset() == 0;
    }

    public boolean validateSimpleWhere(SimpleSqlWhere simpleSqlWhere) {
        SqlNode[] operands = simpleSqlWhere.getOperands();
        SqlExpression operator = simpleSqlWhere.getOperator();
        if (operator == null || ArrayUtils.isEmpty(operands)) {
            return false;
        }

        return Arrays.stream(operands).allMatch(sqlNode -> sqlNode.validate(this, context));
    }

    public boolean validateCompositeWhere(CompositeSqlWhere compositeSqlWhere) {
        String relation = compositeSqlWhere.getRelation();
        List<SqlWhere> conditions = compositeSqlWhere.getConditions();
        if (StringUtils.isEmpty(relation) || CollectionUtils.isEmpty(conditions)) {
            return false;
        }

        return conditions.stream().allMatch(sqlWhere -> sqlWhere.validate(this, context));
    }

    public boolean validateLiteral(SqlLiteral sqlLiteral) {
        return ArrayUtils.isNotEmpty(sqlLiteral.getValues());
    }

    public boolean validateTable(SqlTable sqlTable) {
        return StringUtils.isNoneBlank(sqlTable.getId())
                && StringUtils.isNoneBlank(sqlTable.getDatabase())
                && StringUtils.isNoneBlank(sqlTable.getName());
    }

    public boolean validateFunction(SqlFunction sqlFunction) {
        if (BuildInFunction.of(sqlFunction.getName()) == null) {
            return false;
        }

        List<SqlNode> operands = sqlFunction.getOperands();
        if (CollectionUtils.isEmpty(operands)) {
            return true;
        }

        return operands.stream()
                .allMatch(
                        sqlNode -> {
                            if (sqlNode instanceof SqlTable) {
                                return false;
                            } else {
                                return sqlNode.validate(this, context);
                            }
                        });
    }

    public boolean validateColumn(SqlColumn sqlColumn, DataType dataType) {
        String[] names = sqlColumn.getNames();
        if (names == null || names.length > 2) {
            return false;
        }

        if (dataType instanceof MapDataType) {
            return names.length == 2;
        }

        return true;
    }
}
