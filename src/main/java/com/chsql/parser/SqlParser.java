package com.chsql.parser;

import com.chsql.parser.common.Preconditions;
import com.chsql.parser.common.SqlContext;
import com.chsql.parser.common.TableExtra;
import com.chsql.parser.model.SqlColumn;
import com.chsql.parser.model.SqlFunction;
import com.chsql.parser.model.SqlNode;
import com.chsql.parser.model.SqlSelect;
import com.chsql.parser.model.SqlTable;
import com.chsql.parser.snippet.BuildInFunction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.chsql.parser.util.SqlUtil.extractAllColumns;

/**
 * parse json to sql. <br>
 * thread safe.
 */
public class SqlParser {

    private final SqlContext context;

    private final SqlValidator validator;

    public SqlParser(SqlContext context) {
        this.context = Preconditions.checkNotNull(context);
        this.validator = new SqlValidator(context);
    }

    public String parseQuery(SqlSelect origin) {
        List<SqlTable> sqlTables = extractFrom(origin, context);
        List<SqlNode> groupBy = extractGroupBy(origin.getSelect());

        SqlSelect sqlSelect = new SqlSelect();
        sqlSelect.setSelect(origin.getSelect());
        sqlSelect.setFrom(sqlTables);
        sqlSelect.setWhere(origin.getWhere());
        sqlSelect.setGroupBy(groupBy);
        sqlSelect.setOrderBy(origin.getOrderBy());
        sqlSelect.setLimit(origin.getLimit());

        if (!validator.validate(sqlSelect)) {
            throw new RuntimeException("invalid json string.");
        }

        SqlContext cloneContext = context.clone();
        cloneContext.setSqlSelect(sqlSelect);
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
                            tableExtra.getName(),
                            tableExtra.getDatabase(),
                            tableExtra.getJoinKey());
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
