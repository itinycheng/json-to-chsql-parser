package com.cksql.parser;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.common.TableExtra;
import com.cksql.parser.model.SqlColumn;
import com.cksql.parser.model.SqlFunction;
import com.cksql.parser.model.SqlNode;
import com.cksql.parser.model.SqlSelect;
import com.cksql.parser.model.SqlTable;
import com.cksql.parser.snippet.BuildInFunction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cksql.parser.util.SqlUtil.extractAllColumns;

/**
 * parse json to sql. <br>
 * thread safe.
 */
public class SqlParser {

    public String parseQuery(SqlSelect origin, SqlContext context) {
        List<SqlTable> sqlTables = extractFrom(origin, context);
        List<SqlNode> groupBy = extractGroupBy(origin.getSelect());

        SqlSelect sqlSelect = new SqlSelect();
        sqlSelect.setSelect(origin.getSelect());
        sqlSelect.setFrom(sqlTables);
        sqlSelect.setWhere(sqlSelect.getWhere());
        sqlSelect.setGroupBy(groupBy);
        sqlSelect.setOrderBy(sqlSelect.getOrderBy());
        sqlSelect.setLimit(sqlSelect.getLimit());

        SqlContext cloneContext = context.clone();
        cloneContext.setSqlSelect(sqlSelect);

        new SqlValidator(sqlSelect).validate();
        return sqlSelect.toSQL(cloneContext);
    }

    private List<SqlTable> extractFrom(SqlSelect origin, SqlContext sqlContext) {
        Map<String, SqlTable> tableMap = new LinkedHashMap<>();
        Map<String, TableExtra> allTableMap = sqlContext.getTableMap();
        for (SqlColumn column : extractAllColumns(origin)) {
            TableExtra tableExtra = allTableMap.get(column.getQualifier());
            if (tableExtra == null) {
                throw new RuntimeException("No table extra info found, column: " + column);
            }
            SqlTable sqlTable =
                    new SqlTable(
                            tableExtra.getId().toString(),
                            tableExtra.getDatabase(),
                            tableExtra.getName());
            tableMap.put(column.getQualifier(), sqlTable);
        }
        return new ArrayList<>(tableMap.values());
    }

    private List<SqlNode> extractGroupBy(List<SqlNode> select) {
        List<SqlNode> groupByList = new ArrayList<>();
        List<SqlNode> aggFuncList = new ArrayList<>();
        for (SqlNode sqlNode : select) {
            if (!(sqlNode instanceof SqlFunction)) {
                groupByList.add(sqlNode);
                continue;
            }

            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction buildIn = BuildInFunction.of(sqlFunction.getName());
            if (buildIn.isAggFunc()) {
                aggFuncList.add(sqlNode);
            } else {
                groupByList.add(sqlNode);
            }
        }

        if (CollectionUtils.isEmpty(aggFuncList)) {
            return null;
        }

        return groupByList;
    }
}
