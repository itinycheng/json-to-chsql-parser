package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.cksql.parser.common.Constant.COMMA;
import static com.cksql.parser.common.Constant.EMPTY;

/** sql order by. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlOrderBy {

    private SqlExpression type;

    private List<SqlNode> items;

    public List<SqlColumn> getAllColumns() {
        Set<SqlColumn> columns = new HashSet<>();
        for (SqlNode sqlNode : items) {
            columns.addAll(sqlNode.getColumns());
        }
        return new ArrayList<>(columns);
    }

    public String toSQL(SqlContext context) {
        if (CollectionUtils.isEmpty(items) || type == null) {
            return EMPTY;
        }

        List<String> orderByList = new ArrayList<>();
        List<SqlNode> select = context.getSqlSelect().getSelect();
        for (SqlNode sqlNode : items) {
            if (select.contains(sqlNode)) {
                orderByList.add(sqlNode.ident());
                continue;
            }
            orderByList.add(sqlNode.toSQL(context));
        }

        String joined = String.join(COMMA, orderByList);
        return String.format(type.expression, joined);
    }
}
